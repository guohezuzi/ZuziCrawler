# ZUZI的Java爬虫

## 目的
- 增加自己对多线程的理解
- 爬取一些数据为自己以后学习大数据做铺垫

## 构成
- 使用maven构建 
- 使用httpclient做爬虫客户端
- 使用mongodb储存爬取数据

## 目录结构说明
- ./data 爬取的一些文件数据
- ./src/main/java/util 内置的一些爬虫接口
- ./src/main/java/applications 基于接口调用实现的一些网站数据的爬取的应用
## 功能
- 支持socks代理爬取外网
- 目前实现的可爬取的网站:
	* [p站](./src/main/java/applications/pixiv/Pixiv.java)
## 例子

## Listen
[![License](https://img.shields.io/github/license/mashape/apistatus.svg)](./LICENSE)