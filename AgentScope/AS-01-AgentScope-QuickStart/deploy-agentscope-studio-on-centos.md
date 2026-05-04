<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*

- [1.修复 CentOS7 YUM 源（EOL 必做）](#1%E4%BF%AE%E5%A4%8D-centos7-yum-%E6%BA%90eol-%E5%BF%85%E5%81%9A)
- [2.替换 失效 DNS 为国内公共 DNS](#2%E6%9B%BF%E6%8D%A2-%E5%A4%B1%E6%95%88-dns-%E4%B8%BA%E5%9B%BD%E5%86%85%E5%85%AC%E5%85%B1-dns)
- [3.安装 Docker](#3%E5%AE%89%E8%A3%85-docker)
- [4.安装 docker-compose](#4%E5%AE%89%E8%A3%85-docker-compose)
- [5.部署 AgentScope Studio](#5%E9%83%A8%E7%BD%B2-agentscope-studio)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

### 1.修复 CentOS7 YUM 源（EOL 必做）

修复 YUM 源（解决 CentOS7 EOL 官方源失效问题）。CentOS7 已 EOL，官方 `mirrorlist.centos.org` 常失效。需要改为可用的 Vault
源（示例为阿里云）

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

### 2.替换 失效 DNS 为国内公共 DNS

```bash
# 1. 写入国内公共 DNS（必稳）
sudo tee /etc/resolv.conf <<EOF
nameserver 223.5.5.5
nameserver 119.29.29.29
nameserver 8.8.8.8
EOF

# 2. 防止被系统自动覆盖
sudo chattr +i /etc/resolv.conf
```

### 3.安装 Docker

```bash
#  1.卸载系统可能残留的旧 Docker（全新实例也建议执行）
sudo yum remove -y docker \
                  docker-client \
                  docker-client-latest \
                  docker-common \
                  docker-latest \
                  docker-latest-logrotate \
                  docker-logrotate \
                  docker-engine

# 2.安装依赖工具
sudo yum install -y yum-utils device-mapper-persistent-data lvm2


# 3.添加 阿里云 Docker CE 稳定源（国内最快）
sudo yum-config-manager \
    --add-repo \
    https://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo
    
# 4.安装 Docker 引擎
# sudo yum install -y docker-ce docker-ce-cli containerd.io
# 安装 24.0.9 兼容版（CentOS7 最后支持的稳定版）
# 建议锁定配套版本
sudo yum install -y docker-ce-24.0.9 docker-ce-cli-24.0.9 containerd.io-1.6.33




# 5.启动 Docker 并设置开机自启
sudo systemctl start docker
sudo systemctl enable docker


# 6.验证安装是否成功
docker --version
sudo docker run hello-world


# 7.可选：把当前用户加入 docker 组（不用每次都加 sudo）
# 执行后必须重新登录终端才能生效
sudo usermod -aG docker $USER




# 8.可选：配置阿里云镜像加速（强烈推荐）
sudo tee /etc/docker/daemon.json <<EOF
{
  "registry-mirrors": ["https://docker.mirrors.aliyun.com"]
}
EOF


# 或者
# 目前国内仍可公共使用的加速器已经不多，推荐使用 DaoCloud 镜像站，长期稳定。
sudo tee /etc/docker/daemon.json <<-'EOF'
{
  "registry-mirrors": ["https://docker.m.daocloud.io"]
}
EOF

# 或者
sudo tee /etc/docker/daemon.json <<-'EOF'
{
  "registry-mirrors": [
    "https://docker.m.daocloud.io",
    "https://mirror.baidubce.com",
    "https://docker.nju.edu.cn",
    "https://docker.1ms.run"
  ]
}
EOF



# 重启 Docker 生效
sudo systemctl daemon-reload
sudo systemctl restart docker

# 验证加速器生效
sudo docker info | grep -A 10 "Registry Mirrors"

# 验证
sudo docker run hello-world
```

### 4.安装 docker-compose

```bash
# 1.清理掉之前的错误文件
sudo rm -f /usr/local/bin/docker-compose

# 2.使用可用的国内镜像重新下载
# 推荐使用 DaoCloud 官方提供的镜像加速地址（已验证稳定）
bash
sudo curl -L "https://get.daocloud.io/docker/compose/releases/download/1.29.2/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose

# 或者
# 可以直接从 GitHub 官方下载（国内可能慢）
bash
sudo curl -L "https://github.com/docker/compose/releases/download/1.29.2/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose


# 3.赋予执行权限
sudo chmod +x /usr/local/bin/docker-compose


# 4.验证安装
# 正常应该输出类似 docker-compose version 1.29.2, build ... 的信息
docker-compose --version
```

### 5.部署 AgentScope Studio

```bash
# 1.安装 wget + unzip
sudo yum install -y wget unzip

# 2.回到有权限的家目录
cd ~


# 3.下载 agentscope-studio 源码
sudo wget -O agentscope-studio.zip https://github.com/agentscope-ai/agentscope-studio/archive/refs/heads/main.zip

# 4.创建 /opt/module 目录
sudo mkdir -p /opt/module

# 5.解压到 /opt/module（全路径）
sudo unzip -o ~/agentscope-studio.zip -d /opt/module/

# 6.重命名为标准目录
sudo mv /opt/module/agentscope-studio-main /opt/module/agentscope-studio

# 7.进入 /opt/module/agentscope-studio 目录
cd /opt/module/agentscope-studio

# 8.agentscope-studio 目录提供了包含配置文件的 docker 目录
[vagrant@server05 agentscope-studio]$ ls docker/
docker-compose.yml  Dockerfile  docker.sh  entrypoint.sh  README.md

# 9.执行部署脚本：进入项目根目录后，运行以下命令赋予脚本执行权限并启动服务
# 赋予脚本执行权限
sudo chmod +x docker/docker.sh
# 一键构建镜像并启动后台服务
./docker/docker.sh start
# 可以使用以下命令管理服务状态
# 命令	说明
# ./docker/docker.sh stop	停止服务
# ./docker/docker.sh restart	重启服务
# ./docker/docker.sh logs	查看运行日志
# ./docker/docker.sh status	查看当前服务状态
# ./docker/docker.sh clean	清理资源并重新构建



# 11.访问控制台
# 启动成功后，在浏览器中访问：
# http://<虚拟机IP>:3000
http://192.168.65.11:3000
```



