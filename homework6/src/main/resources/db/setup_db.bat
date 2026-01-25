@echo off
set DB_USER=postgres
set DB_HOST=localhost
set PSQL_PATH="C:\Program Files\PostgreSQL\17\bin\psql.exe"
set DB_NAME=autoservice_db

%PSQL_PATH% -U %DB_USER% -h %DB_HOST% -c "CREATE DATABASE %DB_NAME% ENCODING 'UTF8';"

echo Создание таблиц
%PSQL_PATH% -U %DB_USER% -h %DB_HOST% -d %DB_NAME% -f ddl.sql

echo Заполнение таблиц
%PSQL_PATH% -U %DB_USER% -h %DB_HOST% -d %DB_NAME% -f dml_all.sql

echo База и таблицы созданы
pause