CREATE DATABASE  IF NOT EXISTS `carrinho_certo` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `carrinho_certo`;
-- MySQL dump 10.13  Distrib 8.0.33, for Win64 (x86_64)
--
-- Host: localhost    Database: carrinho_certo
-- ------------------------------------------------------
-- Server version	8.0.34

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `categoria`
--

DROP TABLE IF EXISTS `categoria`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `categoria` (
  `id_categoria` int NOT NULL AUTO_INCREMENT,
  `nome` varchar(60) NOT NULL,
  PRIMARY KEY (`id_categoria`),
  UNIQUE KEY `nome` (`nome`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categoria`
--

LOCK TABLES `categoria` WRITE;
/*!40000 ALTER TABLE `categoria` DISABLE KEYS */;
INSERT INTO `categoria` VALUES (6,'Bebidas'),(5,'Carnes'),(1,'Higiene Pessoal'),(2,'Limpeza'),(3,'Mercearia'),(4,'Verduras e Frutas');
/*!40000 ALTER TABLE `categoria` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `item`
--

DROP TABLE IF EXISTS `item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `item` (
  `id_item` int NOT NULL AUTO_INCREMENT,
  `lista_id` int NOT NULL,
  `categoria_id` int NOT NULL,
  `nome` varchar(100) NOT NULL,
  `quantidade` decimal(8,3) NOT NULL DEFAULT '1.000',
  `unidade` enum('un','kg','g','L','ml','pct','cx','dz') NOT NULL DEFAULT 'un',
  `no_carrinho` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id_item`),
  UNIQUE KEY `uk_item_lista_nome` (`lista_id`,`nome`),
  KEY `fk_item_categoria` (`categoria_id`),
  CONSTRAINT `fk_item_categoria` FOREIGN KEY (`categoria_id`) REFERENCES `categoria` (`id_categoria`) ON DELETE RESTRICT,
  CONSTRAINT `fk_item_lista` FOREIGN KEY (`lista_id`) REFERENCES `lista` (`id_lista`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=40 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `item`
--

LOCK TABLES `item` WRITE;
/*!40000 ALTER TABLE `item` DISABLE KEYS */;
INSERT INTO `item` VALUES (1,1,3,'Arroz Branco',5.000,'kg',1),(2,1,3,'Feijão Carioca',2.000,'kg',0),(3,1,3,'Óleo de Soja',3.000,'un',0),(4,1,2,'Detergente Líquido',2.000,'un',1),(5,1,1,'Creme Dental',4.000,'un',0),(6,2,3,'Carvão',1.000,'pct',0),(7,2,3,'Sal Grosso',1.000,'kg',0),(8,2,3,'Pão de Alho',2.000,'pct',0),(9,2,4,'Limão para Vinagrete',5.000,'un',0),(10,3,4,'Banana Prata',1.000,'dz',1),(11,3,4,'Maçã Fuji',1.500,'kg',1),(12,3,4,'Alface Crespa',2.000,'un',1),(13,3,4,'Tomate Carmem',1.000,'kg',1),(14,9,3,'Arroz',5.000,'kg',0),(18,11,3,'Way',5.000,'kg',0),(24,16,3,'Arroz',1.000,'kg',0),(25,16,3,'Leite',3.000,'cx',0),(28,17,2,'Agua Sanitaria',1.000,'L',0),(30,17,4,'Cenoura',1.500,'kg',0),(33,17,3,'Leite',5.000,'L',0),(37,5,3,'Arroz',1.000,'kg',0),(38,22,3,'Arroz',1.000,'kg',0),(39,22,6,'Coca-Cola (Lata )',12.000,'un',0);
/*!40000 ALTER TABLE `item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lista`
--

DROP TABLE IF EXISTS `lista`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lista` (
  `id_lista` int NOT NULL AUTO_INCREMENT,
  `login_id` int NOT NULL,
  `nome` varchar(100) NOT NULL,
  `data_criacao` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `finalizada` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id_lista`),
  UNIQUE KEY `uk_lista_login_nome` (`login_id`,`nome`),
  CONSTRAINT `fk_lista_login` FOREIGN KEY (`login_id`) REFERENCES `login` (`id_login`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lista`
--

LOCK TABLES `lista` WRITE;
/*!40000 ALTER TABLE `lista` DISABLE KEYS */;
INSERT INTO `lista` VALUES (1,1,'Compras do Mês','2026-08-18 10:05:11',0),(2,1,'Churrasco de Domingo','2026-08-18 10:05:11',0),(3,1,'Feira da Semana','2026-08-18 10:05:11',1),(5,1,'Lista teste2','2026-08-19 09:15:10',0),(6,1,'Lista teste 3','2026-08-19 09:24:00',0),(7,1,'Lista teste 5','2026-08-19 09:27:10',0),(9,1,'Teste testando','2026-08-19 10:41:35',0),(11,1,'Lista do Luiz','2026-08-19 11:11:43',0),(13,1,'Lista manha','2026-08-20 09:30:03',0),(15,1,'Criando lista teste','2026-08-20 09:55:51',0),(16,1,'XXXXX','2026-08-20 10:02:33',0),(17,1,'AAAAAA','2026-08-24 08:36:49',1),(22,1,'lista nova','2026-08-26 08:33:17',1);
/*!40000 ALTER TABLE `lista` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `login`
--

DROP TABLE IF EXISTS `login`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `login` (
  `id_login` int NOT NULL AUTO_INCREMENT,
  `usuario` varchar(45) NOT NULL,
  `senha` varchar(255) NOT NULL,
  `data_cadastro` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_login`),
  UNIQUE KEY `usuario` (`usuario`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `login`
--

LOCK TABLES `login` WRITE;
/*!40000 ALTER TABLE `login` DISABLE KEYS */;
INSERT INTO `login` VALUES (1,'admin','3C9909AFEC25354D551DAE21590BB26E38D53F2173B8D3DC3EEE4C047E7AB1C1EB8B85103E3BE7BA613B31BB5C9C36214DC9F14A42FD7A2FDB84856BCA5C44C2','2026-08-18 09:39:05'),(2,'admin2','3C9909AFEC25354D551DAE21590BB26E38D53F2173B8D3DC3EEE4C047E7AB1C1EB8B85103E3BE7BA613B31BB5C9C36214DC9F14A42FD7A2FDB84856BCA5C44C2','2026-08-24 11:02:04');
/*!40000 ALTER TABLE `login` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'carrinho_certo'
--

--
-- Dumping routines for database 'carrinho_certo'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-08-26 11:20:36
