-- MySQL dump 10.13  Distrib 5.1.41, for debian-linux-gnu (x86_64)
--
-- Host: springtravelsql    Database: springtravelsql
-- ------------------------------------------------------
-- Server version	5.1.41-3ubuntu12.10

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `Booking`
--

DROP TABLE IF EXISTS `Booking`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Booking` (
  `id` bigint(20) NOT NULL,
  `beds` int(11) NOT NULL,
  `checkinDate` date DEFAULT NULL,
  `checkoutDate` date DEFAULT NULL,
  `creditCard` varchar(255) DEFAULT NULL,
  `creditCardExpiryMonth` int(11) NOT NULL,
  `creditCardExpiryYear` int(11) NOT NULL,
  `creditCardName` varchar(255) DEFAULT NULL,
  `smoking` bit(1) NOT NULL,
  `hotel_id` bigint(20) DEFAULT NULL,
  `user_username` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK6713A039BF91BB8D` (`hotel_id`),
  KEY `FK6713A03975CC21E2` (`user_username`),
  CONSTRAINT `FK6713A03975CC21E2` FOREIGN KEY (`user_username`) REFERENCES `Customer` (`username`),
  CONSTRAINT `FK6713A039BF91BB8D` FOREIGN KEY (`hotel_id`) REFERENCES `Hotel` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Booking`
--

LOCK TABLES `Booking` WRITE;
/*!40000 ALTER TABLE `Booking` DISABLE KEYS */;
INSERT INTO `Booking` VALUES (229376,0,'2011-03-24','2011-03-25',NULL,0,0,NULL,'\0',24,'scott'),(589826,3,'2011-04-03','2011-04-04','5747954',3,2013,'josh','',24,'josh');
/*!40000 ALTER TABLE `Booking` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Customer`
--

DROP TABLE IF EXISTS `Customer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Customer` (
  `username` varchar(255) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Customer`
--

LOCK TABLES `Customer` WRITE;
/*!40000 ALTER TABLE `Customer` DISABLE KEYS */;
INSERT INTO `Customer` VALUES ('erwin','Erwin',NULL),('jeremy','Jeremy',NULL),('josh','Josh',NULL),('keith','Keith',NULL),('scott','Scott',NULL);
/*!40000 ALTER TABLE `Customer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Hotel`
--

DROP TABLE IF EXISTS `Hotel`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Hotel` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `address` varchar(255) DEFAULT NULL,
  `city` varchar(255) DEFAULT NULL,
  `country` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `price` decimal(6,2) DEFAULT NULL,
  `state` varchar(255) DEFAULT NULL,
  `zip` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Hotel`
--

LOCK TABLES `Hotel` WRITE;
/*!40000 ALTER TABLE `Hotel` DISABLE KEYS */;
INSERT INTO `Hotel` VALUES (1,'3555 S. Ocean Drive','Hollywood','USA','Westin Diplomat','199.00','FL','33019'),(2,'890 Palm Bay Rd NE','Palm Bay','USA','Jameson Inn','60.00','FL','32905'),(3,'The Cottage, Southampton Business Park','Southampton','UK','Chilworth Manor','199.00','Hants','SO16 7JF'),(4,'Tower Place, Buckhead','Atlanta','USA','Marriott Courtyard','120.00','GA','30305'),(5,'Tower Place, Buckhead','Atlanta','USA','Doubletree','180.00','GA','30305'),(6,'Union Square, Manhattan','NY','USA','W Hotel','450.00','NY','10011'),(7,'Lexington Ave, Manhattan','NY','USA','W Hotel','450.00','NY','10011'),(8,'1315 16th Street NW','Washington','USA','Hotel Rouge','250.00','DC','20036'),(9,'70 Park Avenue','NY','USA','70 Park Avenue Hotel','300.00','NY','10011'),(10,'1395 Brickell Ave','Miami','USA','Conrad Miami','300.00','FL','33131'),(11,'2106 N Clairemont Ave','Eau Claire','USA','Sea Horse Inn','80.00','WI','54703'),(12,'1151 W Macarthur Ave','Eau Claire','USA','Super 8 Eau Claire Campus Area','90.00','WI','54701'),(13,'55 Fourth Street','San Francisco','USA','Marriot Downtown','160.00','CA','94103'),(14,'Passeig del Taulat 262-264','Barcelona','Spain','Hilton Diagonal Mar','200.00','Catalunya','08019'),(15,'Independence Park','Tel Aviv','Israel','Hilton Tel Aviv','210.00','','63405'),(16,'Takeshiba Pier','Tokyo','Japan','InterContinental Tokyo Bay','240.00','','105'),(17,' Esplanade Lopold-Robert 2','Neuchatel','Switzerland','Hotel Beaulac','130.00','','2000'),(18,'William & George Streets','Brisbane','Australia','Conrad Treasury Place','140.00','QLD','4001'),(19,'1228 Sherbrooke St','West Montreal','Canada','Ritz Carlton','230.00','Quebec','H3G1H6'),(20,'Peachtree Rd, Buckhead','Atlanta','USA','Ritz Carlton','460.00','GA','30326'),(21,'68 Market Street','Sydney','Australia','Swissotel','220.00','NSW','2000'),(22,'Albany Street','Regents Park London','Great Britain','Meli White House','250.00','','NW13UP'),(23,'171 West Randolph Street','Chicago','USA','Hotel Allegro','210.00','IL','60601'),(24,'8300 Sunset Boulevard West Hollywood','Los Angeles','USA','The Standard','350.00','CA','90069');
/*!40000 ALTER TABLE `Hotel` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hibernate_sequences`
--

DROP TABLE IF EXISTS `hibernate_sequences`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hibernate_sequences` (
  `sequence_name` varchar(255) DEFAULT NULL,
  `sequence_next_hi_value` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hibernate_sequences`
--

LOCK TABLES `hibernate_sequences` WRITE;
/*!40000 ALTER TABLE `hibernate_sequences` DISABLE KEYS */;
INSERT INTO `hibernate_sequences` VALUES ('Booking',19);
/*!40000 ALTER TABLE `hibernate_sequences` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2011-04-04 11:39:23
