@echo off
set MODULE_NAME=%1

REM Check if a module name is provided
if "%MODULE_NAME%"=="" (
    echo Please provide a module name. Usage: create_module.bat <module_name>
    exit /b 1
)

REM Define the base directory
set BASE_DIR=src\main\java\com\lexi\springapp\%MODULE_NAME%

REM Create folders
mkdir %BASE_DIR%\controller
mkdir %BASE_DIR%\service
mkdir %BASE_DIR%\repository
mkdir %BASE_DIR%\model
mkdir %BASE_DIR%\dto
mkdir %BASE_DIR%\exception
mkdir %BASE_DIR%\util

REM Create default files
echo package com.lexi.%MODULE_NAME%.controller;> %BASE_DIR%\controller\%MODULE_NAME%Controller.java
echo package com.lexi.%MODULE_NAME%.service;> %BASE_DIR%\service\%MODULE_NAME%Service.java
echo package com.lexi.%MODULE_NAME%.repository;> %BASE_DIR%\repository\%MODULE_NAME%Repository.java
echo package com.lexi.%MODULE_NAME%.model;> %BASE_DIR%\model\%MODULE_NAME%.java
echo package com.lexi.%MODULE_NAME%.dto;> %BASE_DIR%\dto\%MODULE_NAME%RequestDto.java
echo package com.lexi.%MODULE_NAME%.dto;> %BASE_DIR%\dto\%MODULE_NAME%ResponseDto.java
echo package com.lexi.%MODULE_NAME%.exception;> %BASE_DIR%\exception\%MODULE_NAME%NotFoundException.java
echo package com.lexi.%MODULE_NAME%.util;> %BASE_DIR%\util\%MODULE_NAME%Util.java

echo Module '%MODULE_NAME%' structure created successfully!
