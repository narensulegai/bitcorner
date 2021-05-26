# Team Project 3
@singhal-akash67 - Akash Singhal(SJSU ID: 015257203) akash.singhal@sjsu.edu

@narensulegai - Naren Janardhan Sulegai(SJSU ID: 014483443) narenjanardhan.sulegai@sjsu.edu

@sowmyadvn - Sowmya Dharani(SJSU ID: 01145168) sowmya.dharanipragada@sjsu.edu

@vandanachandola - Vandana Chandola(SJSU ID: 014748604) vandana.chandola@sjsu.edu

# App URL

http://ec2-3-142-122-215.us-east-2.compute.amazonaws.com:8080/#/

# Build instructions

The appication has been packaged as a jar file, please set the following database connection environment variables to run the jar.

`MYSQL_HOST=<db host> DB_USER=<db user> DB_PASSWORD=<db password>`

  
# You can run locally using

1. Start app.

```
EMAIL_USER=bitcorner275@gmail.com EMAIL_PASSWORD=21Bitcorner275! GOOGLE_APPLICATION_CREDENTIALS=./bitcorner.json MYSQL_HOST=<db host> DB_USER=<db user> DB_PASSWORD=<db password> java -jar ./demo-0.0.1-SNAPSHOT.jar
```


2. Please insert the following default ask prices.

```
INSERT INTO `bitcorner`.`prices` (`id`, `currency`, `latest_ask_price`, `latest_bid_price`, `latest_transaction_price`) VALUES (1, 'USD', 10, 10, 10);
INSERT INTO `bitcorner`.`prices` (`id`, `currency`, `latest_ask_price`, `latest_bid_price`, `latest_transaction_price`) VALUES (2, 'GBP', 10, 10, 10);
INSERT INTO `bitcorner`.`prices` (`id`, `currency`, `latest_ask_price`, `latest_bid_price`, `latest_transaction_price`) VALUES (3, 'INR', 10, 10, 10);
INSERT INTO `bitcorner`.`prices` (`id`, `currency`, `latest_ask_price`, `latest_bid_price`, `latest_transaction_price`) VALUES (4, 'EUR', 10, 10, 10);
INSERT INTO `bitcorner`.`prices` (`id`, `currency`, `latest_ask_price`, `latest_bid_price`, `latest_transaction_price`) VALUES (5, 'RMB', 10, 10, 10);
```

# Source code 
Spring Boot backend https://github.com/narensulegai/bitcorner

ReactJS frontend https://github.com/narensulegai/bitcorner-frontend
