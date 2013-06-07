CREATE TABLE wifi_user(account TEXT PRIMARY KEY NOT NULL, password TEXT, isDefault INTEGER,
 balance DECIMAL(10,2), expireTime date, leftTime LONG, userState INTEGER, productId INTEGER);
 
CREATE TABLE product(id INTEGER PRIMARY KEY, productName TEXT, billTime INTEGER, timeUnit TEXT, moneyCount DOUBLE(12,3), baseFee INTEGER, maxFee INTEGER, isLimitFee INTEGER, maxUpBytes INTEGER, maxDownByte INTEGER);

CREATE TABLE charge_record(id INTEGER PRIMARY KEY, account TEXT, chargeCount DECIMAL(10,2), chargeTime DATETIME, username TEXT);

CREATE TABLE bill(id INTEGER PRIMARY KEY, account TEXT,billTime LONG, billCount DECIMAL(10,2), totalBytes LONG, createDate DATE);

CREATE TABLE traffic _soft(id INTEGER PRIMARY KEY, softName TEXT, softLogo TEXT, phoneTraffic DOUBLE(12,3), wifiTraffic DOUBLE(12,3), createDate DATETIME);

CREATE TABLE traffic_daily(id INTEGER PRIMARY KEY, phoneTraffic DOUBLE(12,3), wifiTraffic DOUBLE(12,3), createDate DATETIME);

CREATE TABLE ad_record(id INTEGER PRIMARY KEY, adInfo TEXT, location TEXT, createDate TEXT, adUrl TEXT, bssid TEXT);

CREATE TABLE wlfinger(id INTEGER PRIMARY KEY, serveURL TEXT, SSID TEXT, collect_period TEXT, collect_rate TEXT, creDate TEXT);