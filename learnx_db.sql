/*
 Navicat Premium Dump SQL

 Source Server         : localhost_3316
 Source Server Type    : MySQL
 Source Server Version : 80042 (8.0.42)
 Source Host           : localhost:3316
 Source Schema         : learnx_db

 Target Server Type    : MySQL
 Target Server Version : 80042 (8.0.42)
 File Encoding         : 65001

 Date: 25/05/2025 19:02:44
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for assignment
-- ----------------------------
DROP TABLE IF EXISTS `assignment`;
CREATE TABLE `assignment`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `created_by` bigint NOT NULL,
  `updated_at` datetime(6) NULL DEFAULT NULL,
  `updated_by` bigint NULL DEFAULT NULL,
  `closed_date` datetime(6) NULL DEFAULT NULL,
  `content` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `end_date` datetime(6) NULL DEFAULT NULL,
  `start_date` datetime(6) NULL DEFAULT NULL,
  `state` enum('ACCEPTED','ACTIVE','CLOSED','DELETED','FAILED','INACTIVE','NOT_SUBMITTED','OPEN','PASSED','PENDING','REJECTED','SUBMITTED') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `url_document` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `module_id` bigint NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FK_assignment_module`(`module_id` ASC) USING BTREE,
  CONSTRAINT `FK_assignment_module` FOREIGN KEY (`module_id`) REFERENCES `module` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for assignment_submission
-- ----------------------------
DROP TABLE IF EXISTS `assignment_submission`;
CREATE TABLE `assignment_submission`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `created_by` bigint NOT NULL,
  `updated_at` datetime(6) NULL DEFAULT NULL,
  `updated_by` bigint NULL DEFAULT NULL,
  `file_submission_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `score` double NULL DEFAULT NULL,
  `text_submission` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `assignment_id` bigint NULL DEFAULT NULL,
  `student_id` bigint NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FKi9tdkyaqlb4j7qm7y2k74jd7o`(`assignment_id` ASC) USING BTREE,
  INDEX `FKqsi9w45eary4louu2eds5a1r7`(`student_id` ASC) USING BTREE,
  CONSTRAINT `FKi9tdkyaqlb4j7qm7y2k74jd7o` FOREIGN KEY (`assignment_id`) REFERENCES `assignment` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FKqsi9w45eary4louu2eds5a1r7` FOREIGN KEY (`student_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for category
-- ----------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `created_by` bigint NOT NULL,
  `updated_at` datetime(6) NULL DEFAULT NULL,
  `updated_by` bigint NULL DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UK46ccwnsi9409t36lurvtyljak`(`name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for change_role_queue
-- ----------------------------
DROP TABLE IF EXISTS `change_role_queue`;
CREATE TABLE `change_role_queue`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `created_by` bigint NOT NULL,
  `updated_at` datetime(6) NULL DEFAULT NULL,
  `updated_by` bigint NULL DEFAULT NULL,
  `new_role` enum('ADMIN','STUDENT','TEACHER') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `old_role` enum('ADMIN','STUDENT','TEACHER') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `status` enum('ACCEPTED','ACTIVE','CLOSED','DELETED','FAILED','INACTIVE','NOT_SUBMITTED','OPEN','PASSED','PENDING','REJECTED','SUBMITTED') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `user_id` bigint NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UKmyktqgrpeyj8ar5cjm6iqqpg2`(`user_id` ASC) USING BTREE,
  CONSTRAINT `FKb6sl4jmhv2610xrfue0bocdry` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for course
-- ----------------------------
DROP TABLE IF EXISTS `course`;
CREATE TABLE `course`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `created_by` bigint NOT NULL,
  `updated_at` datetime(6) NULL DEFAULT NULL,
  `updated_by` bigint NULL DEFAULT NULL,
  `description` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `start_date` datetime(6) NULL DEFAULT NULL,
  `state` enum('ACCEPTED','ACTIVE','CLOSED','DELETED','FAILED','INACTIVE','NOT_SUBMITTED','OPEN','PASSED','PENDING','REJECTED','SUBMITTED') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `thumbnail` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `category_id` bigint NULL DEFAULT NULL,
  `teacher_id` bigint NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FK_course_category`(`category_id` ASC) USING BTREE,
  INDEX `FKbhmlx82vjuwypl8dmfnrbjfhu`(`teacher_id` ASC) USING BTREE,
  CONSTRAINT `FK_course_category` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FKbhmlx82vjuwypl8dmfnrbjfhu` FOREIGN KEY (`teacher_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for course_registration
-- ----------------------------
DROP TABLE IF EXISTS `course_registration`;
CREATE TABLE `course_registration`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `created_by` bigint NOT NULL,
  `updated_at` datetime(6) NULL DEFAULT NULL,
  `updated_by` bigint NULL DEFAULT NULL,
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `state` enum('ACCEPTED','ACTIVE','CLOSED','DELETED','FAILED','INACTIVE','NOT_SUBMITTED','OPEN','PASSED','PENDING','REJECTED','SUBMITTED') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `total_gpa` double NULL DEFAULT NULL,
  `course_id` bigint NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FKkcpyqpea6srulkxuhvrwrvhow`(`course_id` ASC) USING BTREE,
  CONSTRAINT `FKkcpyqpea6srulkxuhvrwrvhow` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for forgot_password_tokens
-- ----------------------------
DROP TABLE IF EXISTS `forgot_password_tokens`;
CREATE TABLE `forgot_password_tokens`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `expired_at` datetime(6) NULL DEFAULT NULL,
  `token` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `user_id` bigint NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UKat9mrjt361fm34lssgjpwxko3`(`user_id` ASC) USING BTREE,
  CONSTRAINT `FKjtwr6nb8348mo7s08jr01o6ag` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for forum
-- ----------------------------
DROP TABLE IF EXISTS `forum`;
CREATE TABLE `forum`  (
  `course_id` bigint NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `created_by` bigint NOT NULL,
  `updated_at` datetime(6) NULL DEFAULT NULL,
  `updated_by` bigint NULL DEFAULT NULL,
  `description` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  PRIMARY KEY (`course_id`) USING BTREE,
  CONSTRAINT `FK_forum_course` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for lecture
-- ----------------------------
DROP TABLE IF EXISTS `lecture`;
CREATE TABLE `lecture`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `created_by` bigint NOT NULL,
  `updated_at` datetime(6) NULL DEFAULT NULL,
  `updated_by` bigint NULL DEFAULT NULL,
  `content` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `module_id` bigint NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FK_lecture_modules`(`module_id` ASC) USING BTREE,
  CONSTRAINT `FK_lecture_modules` FOREIGN KEY (`module_id`) REFERENCES `module` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for module
-- ----------------------------
DROP TABLE IF EXISTS `module`;
CREATE TABLE `module`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `created_by` bigint NOT NULL,
  `updated_at` datetime(6) NULL DEFAULT NULL,
  `updated_by` bigint NULL DEFAULT NULL,
  `description` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `course_id` bigint NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FK_course_module`(`course_id` ASC) USING BTREE,
  CONSTRAINT `FK_course_module` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for notification
-- ----------------------------
DROP TABLE IF EXISTS `notification`;
CREATE TABLE `notification`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `created_by` bigint NOT NULL,
  `message` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `updated_at` datetime(6) NULL DEFAULT NULL,
  `updated_by` bigint NULL DEFAULT NULL,
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `user_id` bigint NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FKnk4ftb5am9ubmkv1661h15ds9`(`user_id` ASC) USING BTREE,
  CONSTRAINT `FKnk4ftb5am9ubmkv1661h15ds9` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for question
-- ----------------------------
DROP TABLE IF EXISTS `question`;
CREATE TABLE `question`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `created_by` bigint NOT NULL,
  `updated_at` datetime(6) NULL DEFAULT NULL,
  `updated_by` bigint NULL DEFAULT NULL,
  `content` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `question_type` tinyint NULL DEFAULT NULL,
  `score` double NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 29 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for question_answer
-- ----------------------------
DROP TABLE IF EXISTS `question_answer`;
CREATE TABLE `question_answer`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `created_by` bigint NOT NULL,
  `updated_at` datetime(6) NULL DEFAULT NULL,
  `updated_by` bigint NULL DEFAULT NULL,
  `answer_content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `answer_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `question_id` bigint NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FKflwcda2rengsndju5f1deywok`(`question_id` ASC) USING BTREE,
  CONSTRAINT `FKflwcda2rengsndju5f1deywok` FOREIGN KEY (`question_id`) REFERENCES `question` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 37 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for question_option
-- ----------------------------
DROP TABLE IF EXISTS `question_option`;
CREATE TABLE `question_option`  (
  `option_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `question_id` bigint NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `created_by` bigint NOT NULL,
  `updated_at` datetime(6) NULL DEFAULT NULL,
  `updated_by` bigint NULL DEFAULT NULL,
  `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `seq` int NOT NULL,
  PRIMARY KEY (`option_id`, `question_id`) USING BTREE,
  INDEX `FKmmdv54rmm5hkgxbn1008ix87n`(`question_id` ASC) USING BTREE,
  CONSTRAINT `FKmmdv54rmm5hkgxbn1008ix87n` FOREIGN KEY (`question_id`) REFERENCES `question` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for quiz
-- ----------------------------
DROP TABLE IF EXISTS `quiz`;
CREATE TABLE `quiz`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `created_by` bigint NOT NULL,
  `updated_at` datetime(6) NULL DEFAULT NULL,
  `updated_by` bigint NULL DEFAULT NULL,
  `attempt_allowed` int NULL DEFAULT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `end_date` datetime(6) NULL DEFAULT NULL,
  `is_shuffled` bit(1) NOT NULL,
  `start_date` datetime(6) NULL DEFAULT NULL,
  `status` bit(1) NOT NULL,
  `time_limit` int NULL DEFAULT NULL,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `module_id` bigint NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FKd3pi74lbnexp6xjb3595p9088`(`module_id` ASC) USING BTREE,
  CONSTRAINT `FKd3pi74lbnexp6xjb3595p9088` FOREIGN KEY (`module_id`) REFERENCES `module` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for quiz_question
-- ----------------------------
DROP TABLE IF EXISTS `quiz_question`;
CREATE TABLE `quiz_question`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `seq` int NOT NULL,
  `question_id` bigint NULL DEFAULT NULL,
  `quiz_id` bigint NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FK62empq7vfu15qv1kci624f1js`(`question_id` ASC) USING BTREE,
  INDEX `FKdtynvfjgh6e7fd8l0wk37nrpc`(`quiz_id` ASC) USING BTREE,
  CONSTRAINT `FK62empq7vfu15qv1kci624f1js` FOREIGN KEY (`question_id`) REFERENCES `question` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FKdtynvfjgh6e7fd8l0wk37nrpc` FOREIGN KEY (`quiz_id`) REFERENCES `quiz` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 29 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for quiz_sessions
-- ----------------------------
DROP TABLE IF EXISTS `quiz_sessions`;
CREATE TABLE `quiz_sessions`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `end_time` datetime(6) NOT NULL,
  `start_time` datetime(6) NOT NULL,
  `status` enum('ACTIVE','COMPLETED','EXPIRED') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `total_time_taken_seconds` int NULL DEFAULT NULL,
  `quiz_id` bigint NOT NULL,
  `student_id` bigint NOT NULL,
  `submission_id` bigint NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UK220fwi720yrb066ykupfaip7w`(`submission_id` ASC) USING BTREE,
  INDEX `FK6q02prcfqy6554sv1hste9xy7`(`quiz_id` ASC) USING BTREE,
  INDEX `FKe9464vcq03okrxalgrt65f6ib`(`student_id` ASC) USING BTREE,
  CONSTRAINT `FK6q02prcfqy6554sv1hste9xy7` FOREIGN KEY (`quiz_id`) REFERENCES `quiz` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FKe5n84a82iw68r96ylh7gq18q4` FOREIGN KEY (`submission_id`) REFERENCES `quiz_submission` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FKe9464vcq03okrxalgrt65f6ib` FOREIGN KEY (`student_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for quiz_submission
-- ----------------------------
DROP TABLE IF EXISTS `quiz_submission`;
CREATE TABLE `quiz_submission`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `created_by` bigint NOT NULL,
  `updated_at` datetime(6) NULL DEFAULT NULL,
  `updated_by` bigint NULL DEFAULT NULL,
  `score` double NULL DEFAULT NULL,
  `total_times` int NULL DEFAULT NULL,
  `quiz_id` bigint NULL DEFAULT NULL,
  `student_id` bigint NULL DEFAULT NULL,
  `total_time` int NULL DEFAULT NULL,
  `total_time_taken_seconds` int NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FK_quiz_submission_quiz`(`quiz_id` ASC) USING BTREE,
  INDEX `FK_quiz_submission_account`(`student_id` ASC) USING BTREE,
  CONSTRAINT `FK_quiz_submission_account` FOREIGN KEY (`student_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FK_quiz_submission_quiz` FOREIGN KEY (`quiz_id`) REFERENCES `quiz` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for quiz_submission_answer
-- ----------------------------
DROP TABLE IF EXISTS `quiz_submission_answer`;
CREATE TABLE `quiz_submission_answer`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `created_by` bigint NOT NULL,
  `updated_at` datetime(6) NULL DEFAULT NULL,
  `updated_by` bigint NULL DEFAULT NULL,
  `answer` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `quiz_submission_detail_id` bigint NULL DEFAULT NULL,
  `is_correct` bit(1) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FK8hr48fumqogvugif3h6earq4x`(`quiz_submission_detail_id` ASC) USING BTREE,
  CONSTRAINT `FK8hr48fumqogvugif3h6earq4x` FOREIGN KEY (`quiz_submission_detail_id`) REFERENCES `quiz_submission_detail` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 55 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for quiz_submission_detail
-- ----------------------------
DROP TABLE IF EXISTS `quiz_submission_detail`;
CREATE TABLE `quiz_submission_detail`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `created_by` bigint NOT NULL,
  `updated_at` datetime(6) NULL DEFAULT NULL,
  `updated_by` bigint NULL DEFAULT NULL,
  `question_id` bigint NULL DEFAULT NULL,
  `quiz_submission_id` bigint NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FK8yfvsbsyv3mo022r68q7qe7xr`(`question_id` ASC) USING BTREE,
  INDEX `FKidgatxl9ug7iv6o4sg78jhaq5`(`quiz_submission_id` ASC) USING BTREE,
  CONSTRAINT `FK8yfvsbsyv3mo022r68q7qe7xr` FOREIGN KEY (`question_id`) REFERENCES `question` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FKidgatxl9ug7iv6o4sg78jhaq5` FOREIGN KEY (`quiz_submission_id`) REFERENCES `quiz_submission` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 43 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for refresh_token
-- ----------------------------
DROP TABLE IF EXISTS `refresh_token`;
CREATE TABLE `refresh_token`  (
  `id_refresh_token` bigint NOT NULL AUTO_INCREMENT,
  `expiry_date` datetime(6) NULL DEFAULT NULL,
  `token` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `id_user` bigint NULL DEFAULT NULL,
  PRIMARY KEY (`id_refresh_token`) USING BTREE,
  UNIQUE INDEX `UKjakfiqlvxpatgjgqsgs310opg`(`id_user` ASC) USING BTREE,
  CONSTRAINT `FK9ga6lxgynm512xwl8ie5vb6n0` FOREIGN KEY (`id_user`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 29 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for resources
-- ----------------------------
DROP TABLE IF EXISTS `resources`;
CREATE TABLE `resources`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `created_by` bigint NOT NULL,
  `updated_at` datetime(6) NULL DEFAULT NULL,
  `updated_by` bigint NULL DEFAULT NULL,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `url_document` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `module_id` bigint NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FK_resources_modules`(`module_id` ASC) USING BTREE,
  CONSTRAINT `FK_resources_modules` FOREIGN KEY (`module_id`) REFERENCES `module` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tokens
-- ----------------------------
DROP TABLE IF EXISTS `tokens`;
CREATE TABLE `tokens`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `confirmed_at` datetime(6) NULL DEFAULT NULL,
  `created_at` datetime(6) NULL DEFAULT NULL,
  `expired_at` datetime(6) NULL DEFAULT NULL,
  `token` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `user_id` bigint NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FK2dylsfo39lgjyqml2tbe0b0ss`(`user_id` ASC) USING BTREE,
  CONSTRAINT `FK2dylsfo39lgjyqml2tbe0b0ss` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for topic
-- ----------------------------
DROP TABLE IF EXISTS `topic`;
CREATE TABLE `topic`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `created_by` bigint NOT NULL,
  `updated_at` datetime(6) NULL DEFAULT NULL,
  `updated_by` bigint NULL DEFAULT NULL,
  `content` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `account_id` bigint NULL DEFAULT NULL,
  `forum_id` bigint NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FK_topic_account`(`account_id` ASC) USING BTREE,
  INDEX `FK_topic_forum`(`forum_id` ASC) USING BTREE,
  CONSTRAINT `FK_topic_account` FOREIGN KEY (`account_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FK_topic_forum` FOREIGN KEY (`forum_id`) REFERENCES `forum` (`course_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for topic_comment
-- ----------------------------
DROP TABLE IF EXISTS `topic_comment`;
CREATE TABLE `topic_comment`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `created_by` bigint NOT NULL,
  `updated_at` datetime(6) NULL DEFAULT NULL,
  `updated_by` bigint NULL DEFAULT NULL,
  `content` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `account_id` bigint NULL DEFAULT NULL,
  `topic_id` bigint NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FKamqn2ojmvtnmue3t7jqb36wci`(`account_id` ASC) USING BTREE,
  INDEX `FKcvkrpo61w27xnga2w2tx12ue7`(`topic_id` ASC) USING BTREE,
  CONSTRAINT `FKamqn2ojmvtnmue3t7jqb36wci` FOREIGN KEY (`account_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FKcvkrpo61w27xnga2w2tx12ue7` FOREIGN KEY (`topic_id`) REFERENCES `topic` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `avatar_url` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `full_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `is_enabled` bit(1) NOT NULL,
  `last_login` datetime(6) NULL DEFAULT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `role` enum('ADMIN','STUDENT','TEACHER') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `updated_at` datetime(6) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UK6dotkott2kjsp8vw4d0m25fb7`(`email` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
