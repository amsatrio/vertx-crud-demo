CREATE TABLE IF NOT EXISTS `m_biodata` (
  `id` bigint NOT NULL,
  `fullname` varchar(128) DEFAULT NULL,
  `mobile_phone` varchar(18) DEFAULT NULL,
  `image` blob DEFAULT NULL,
  `image_path` longtext DEFAULT NULL,
  `created_by` bigint NOT NULL,
  `modified_by` bigint DEFAULT NULL,
  `deleted_by` bigint DEFAULT NULL,
  `created_on` datetime NOT NULL,
  `modified_on` datetime DEFAULT NULL,
  `deleted_on` datetime DEFAULT NULL,
  `is_delete` tinyint DEFAULT NULL,
  PRIMARY KEY (`id`,`created_on`)
)