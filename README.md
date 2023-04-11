
# Iot-shop-inventor

A softawre solution for iot shop inventory.

## Requirements
* Docker
* Docker-Compose

## Installation

to install the project you should go to project diractory then run these commends 

```bash
  docker-compose build
```
```bash
  docker-compose up -d
```   
after createing the docker containers you must connect to database container and run set-up quiers in db.sql file.

## Database Set Up

* Connect to database container.
```bash
  docker container exec -it db bash
```

* Login with user `postgres`.
```bash
   psql -U postgres
```

* Run script in the `db.sql` 'line by line'.
```bash
   \c  inventory;
```
```bash
   CREATE TYPE status_type AS ENUM('READY', 'ACTIVE');
```
```bash
   CREATE CAST (character varying as status_type) WITH INOUT AS IMPLICIT;
```

```bash
   CREATE TABLE IF NOT EXISTS devices (
    id SERIAL PRIMARY KEY,
    pin_code CHAR(7) NOT NULL,
    status status_type NOT NULL DEFAULT 'READY'::status_type,
    temperature int NOT NULL DEFAULT -1,
    created_at TIMESTAMP DEFAULT NOW(),
    UNIQUE(pin_code)
);
```



## API's

Find all API's in this [Postman Collection](https://documenter.getpostman.com/view/26815071/2s93Xu1R6v).

## Notes :-

1- Status column is an Enum column as we have to status values , but if we will have more we can make a new table of it (3nf).

2- Device Configuration Service can be implemeted as seperate Micro-Service.

3- If you want to change database (name - user - password) change it from `.env` file.
