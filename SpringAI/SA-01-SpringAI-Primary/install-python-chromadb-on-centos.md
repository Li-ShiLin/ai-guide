CentOS7 编译安装 Python3.10 并部署 ChromaDB

### 1.环境概述

- **系统版本**：CentOS Linux 7.8.2003（Core）
- **文档概述**：在 CentOS7 上通过源码编译安装 `OpenSSL 1.1.1w`、`SQLite 3.42.0`、`Python 3.10.14`，并成功运行 `ChromaDB` 服务。
- **细节说明**：
  - `OpenSSL 1.1.1w`：CentOS7 默认 OpenSSL 版本较低，容易导致 Python 的 SSL 模块编译失败。
  - `SQLite 3.42.0`：ChromaDB 依赖较新 SQLite，系统自带版本通常不满足要求。
  - 系统 GCC 版本较低，需用 `devtoolset-10` 升级编译环境。

### 2.修复 CentOS7 YUM 源（EOL 必做）

修复 YUM 源（解决 CentOS7 EOL 官方源失效问题）。CentOS7 已 EOL，官方 `mirrorlist.centos.org` 常失效。需要改为可用的 Vault 源（示例为阿里云）

```bash
# CentOS7 已停止维护，官方 mirrorlist.centos.org 已无法解析，必须替换为阿里云 Vault 源


# 检查系统版本
[vagrant@server04 ~]$ cat /etc/centos-release
CentOS Linux release 7.8.2003 (Core)
[vagrant@server04 ~]$


# 1. 清空所有旧的 repo 配置，避免残留干扰
sudo rm -rf /etc/yum.repos.d/*

# 2. 写入完整的 Vault 源配置，彻底抛弃 mirrorlist
sudo tee /etc/yum.repos.d/CentOS-Vault.repo <<EOF
[base]
name=CentOS-7 - Base - Vault
baseurl=http://mirrors.aliyun.com/centos-vault/7.9.2009/os/\$basearch/
gpgcheck=1
gpgkey=http://mirrors.aliyun.com/centos/RPM-GPG-KEY-CentOS-7
enabled=1

[updates]
name=CentOS-7 - Updates - Vault
baseurl=http://mirrors.aliyun.com/centos-vault/7.9.2009/updates/\$basearch/
gpgcheck=1
gpgkey=http://mirrors.aliyun.com/centos/RPM-GPG-KEY-CentOS-7
enabled=1

[extras]
name=CentOS-7 - Extras - Vault
baseurl=http://mirrors.aliyun.com/centos-vault/7.9.2009/extras/\$basearch/
gpgcheck=1
gpgkey=http://mirrors.aliyun.com/centos/RPM-GPG-KEY-CentOS-7
enabled=1

[centos-sclo-sclo]
name=CentOS-7 - SCLo sclo - Vault
baseurl=http://mirrors.aliyun.com/centos-vault/7.9.2009/sclo/x86_64/sclo/
gpgcheck=1
gpgkey=http://mirrors.aliyun.com/centos/RPM-GPG-KEY-CentOS-7
enabled=1

[centos-sclo-rh]
name=CentOS-7 - SCLo rh - Vault
baseurl=http://mirrors.aliyun.com/centos-vault/7.9.2009/sclo/x86_64/rh/
gpgcheck=1
gpgkey=http://mirrors.aliyun.com/centos/RPM-GPG-KEY-CentOS-7
enabled=1
EOF

# 3. 清理缓存，重新生成
sudo yum clean all
sudo yum makecache
```

### 3.准备编译环境

```bash
# 安装 yum-config-manager 和 curl，用于后续源管理及文件下载
sudo yum install -y yum-utils curl
sudo yum install wget -y

# 创建统一安装目录
# 进入安装目录（不存在则自动创建）
sudo mkdir -p /opt/module && cd /opt/module
sudo chown vagrant:vagrant /opt/module


# 安装基础依赖
sudo yum install -y gcc gcc-c++ make perl zlib-devel libffi-devel

# 安装 devtoolset-10 升级 GCC 版本（系统自带 GCC 版本过低，无法编译 Python3.10）
sudo yum install -y devtoolset-10-gcc devtoolset-10-gcc-c++
# 或者
sudo yum install -y devtoolset-10-gcc devtoolset-10-gcc-c++ --nogpgcheck


# 加载新的 GCC 环境（必须执行，否则还是用系统老版本 GCC）
source /opt/rh/devtoolset-10/enable
```

### 4.编译安装 OpenSSL 1.1.1w

编译安装高版本 OpenSSL（解决 Python SSL 模块编译失败）

```bash
# 系统自带 OpenSSL 1.0.2 版本过低，Python3.10 无法正常编译 SSL 模块
# 编译安装 OpenSSL 1.1.1w

# 1.进入 /opt/module 目录
cd /opt/module


# 2.下载 OpenSSL 源码
sudo wget https://www.openssl.org/source/openssl-1.1.1w.tar.gz
# 或者 
# 在 Windows 上下载这个文件。下载地址：https://www.openssl.org/source/openssl-1.1.1w.tar.gz
# win11上传Linux服务器
scp -i "D:\abcd\VM\server04\.vagrant\machines\default\virtualbox\private_key" "C:\Downloads\openssl-1.1.1w.tar.gz" vagrant@192.168.56.14:/home/vagrant/


# 3.解压
sudo tar -zxvf  openssl-1.1.1w.tar.gz -C /opt/module
cd /opt/module/openssl-1.1.1w


# 4.编译安装 OpenSSL 1.1.1w
# 配置编译，安装到 /opt/module/openssl11
cd /opt/module/openssl-1.1.1w
sudo ./config --prefix=/opt/module/openssl11 --openssldir=/opt/module/openssl11 shared zlib
# 编译
sudo make -j$(nproc)
# 安装
sudo make install


# 5.把 OpenSSL 的库路径添加到系统动态库搜索路径
echo "/opt/module/openssl11/lib" | sudo tee /etc/ld.so.conf.d/openssl11.conf
sudo ldconfig


# 6.验证
/opt/module/openssl11/bin/openssl version
# 输出：OpenSSL 1.1.1w  11 Sep 2023 说明成功
```

### 5.编译安装 SQLite 3.42.0

编译安装高版本 SQLite（解决 ChromaDB 版本依赖）

```bash
# ChromaDB 要求 SQLite >= 3.39.0
# 编译安装新版 SQLite 3.42.0（编译到 /opt/module）
# 1.下载 sqlite3 3.42.0（稳定版，满足 ChromaDB）
cd /opt/module
sudo wget https://www.sqlite.org/2023/sqlite-autoconf-3420000.tar.gz
# 或者
# 在 Windows 上下载这个文件。SQLite 下载地址：https://www.sqlite.org/2023/sqlite-autoconf-3420000.tar.gz
# win11上传Linux服务器
# scp -i "D:\abcd\VM\server04\.vagrant\machines\default\virtualbox\private_key" "C:\Users\22418\Downloads\sqlite-autoconf-3420000.tar.gz" vagrant@192.168.56.14:/home/vagrant/


# 3.解压
sudo tar -zxvf sqlite-autoconf-3420000.tar.gz -C /opt/module
cd /opt/module/sqlite-autoconf-3420000

# 4.编译安装
sudo ./configure --prefix=/usr/local
sudo make -j$(nproc)
sudo make install


# 5.验证
/usr/local/bin/sqlite3 --version
# 输出：3.42.0 说明成功
```

### 6. 编译安装 Python 3.10.14

下载 Python 3.10 到 /opt/module/ 并解压

```bash
# 1.下载 Python 3.10 到 /opt/module/ 并解压
cd /opt/module
# 下载
sudo wget https://www.python.org/ftp/python/3.10.14/Python-3.10.14.tgz
# 或者
# 通过win11系统下载后再上传Linux
# https://www.python.org/ftp/python/3.10.14/Python-3.10.14.tgz



# 2.解压到 /opt/module/
sudo tar -zxvf Python-3.10.14.tgz -C /opt/module/


# 3.修改权限
sudo chown -R vagrant:vagrant /opt/module/Python-3.10.14


# 4.进入源码目录
cd /opt/module/Python-3.10.14


# 5.清理旧编译缓存
# 如果之前编译失败过，先彻底清理残留
make distclean
rm -rf build
```

配置并编译 Python 3.10.14

```bash
# 1.重新编译 Python 3.10.14（绑定新版 sqlite3）
cd /opt/module/Python-3.10.14

# 2.清理旧编译
make distclean
rm -rf build

# 3.加载高版本 GCC
source /opt/rh/devtoolset-10/enable


# 4.关键：配置 Python 启用新版 sqlite3 + OpenSSL
./configure --prefix=/usr/local \
--enable-optimizations \
--enable-shared \
--with-openssl=/opt/module/openssl11 \
CPPFLAGS="-I/opt/module/openssl11/include -I/usr/local/include" \
LDFLAGS="-L/opt/module/openssl11/lib -L/usr/local/lib -Wl,-rpath=/opt/module/openssl11/lib -Wl,-rpath=/usr/local/lib"


# 5.编译安装
make -j$(nproc)


sudo make altinstall
# 或者
sudo make install

# sudo make altinstall 和 sudo make install 对比
# 1.sudo make install 
# 会覆盖系统默认的 Python 版本
# 将新安装的 Python 设置为系统的 python 和 python3 命令
# 可能导致系统工具崩溃，因为许多系统工具（如 apt、yum、systemd）依赖于特定版本的系统 Python
# 适用于希望将新版本作为系统唯一 Python 版本的场景
# 2.sudo make altinstall
# 不会覆盖系统默认的 Python 版本
# 仅安装特定版本的 Python（如 python3.10、python3.11 命令）
# 保持系统 Python 环境稳定，避免影响系统工具
# 适用于需要多版本 Python 共存的开发环境
# 3.总结：在生产环境中，强烈建议使用 make altinstall 而不是 make install，以避免因 Python 版本冲突导致的系统不稳定问题。系统 Python 是许多关键工具的依赖，随意覆盖可能导致难以排查的问题。 




# 6.刷新动态库
echo "/usr/local/lib" | sudo tee /etc/ld.so.conf.d/local.conf
sudo ldconfig

# 7.验证 Python & pip
python3.10 --version
pip3.10 --version


# 8.验证 sqlite3 版本（必须 ≥3.35.0）
python3.10 -c "import sqlite3; print(sqlite3.sqlite_version)"
# 输出 3.42.0 就成功了



# 9.让系统直接能用 pip 和 python3
echo "alias python3='python3.10'" >> ~/.bashrc
echo "alias pip='pip3.10'" >> ~/.bashrc
source ~/.bashrc
# 之后就可以直接用
pip install chromadb
python3
```

### 7.安装并启动 ChromaDB

```bash
# 1.安装并启动 ChromaDB
sudo pip3.10 install chromadb
# 如果下载慢，用国内镜像
sudo python3.10 -m pip install chromadb -i http://pypi.tuna.tsinghua.edu.cn/simple --trusted-host pypi.tuna.tsinghua.edu.cn


sudo mkdir -p /opt/module/chroma_data
# 给当前用户权限，避免运行时无法写入
sudo chown vagrant:vagrant /opt/module/chroma_data



# 2.开放防火墙端口（必须做！否则其他机器连不上）
# 执行下面两条命令，开放 8000 端口：
sudo firewall-cmd --add-port=8000/tcp --permanent
sudo firewall-cmd --reload
# 如果没有开启防火墙的话，执行下面的命令
sudo systemctl start firewalld



# 3.启动chromadb服务
# 方式 1：直接使用 chroma 命令（推荐）
# 格式：chroma run --path 你的数据存储目录 --host 监听地址 --port 端口
chroma run --path /opt/module/chroma_data --host 0.0.0.0 --port 8000

# 方式 2：如果提示找不到 chroma 命令，用 Python 模块方式运行（100% 成功）
python3.10 -m chromadb.cli.cli run --path /opt/module/chroma_data --host 0.0.0.0 --port 8000

# 后台运行（生产环境推荐）
# 如果想让 ChromaDB 后台运行，不占用终端，用这个命令
nohup chroma run --path /opt/module/chroma_data --host 0.0.0.0 --port 8000 > /opt/module/chroma_data/chroma.log 2>&1 &


# 4.访问测试
http://192.168.56.14:8000/api/v2/heartbeat
```

