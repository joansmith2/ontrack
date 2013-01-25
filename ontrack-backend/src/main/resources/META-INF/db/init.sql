-- DB versioning
CREATE TABLE DBVERSION (
	VALUE INTEGER NOT NULL,
	UPDATED TIMESTAMP NOT NULL
);

-- Accounts
CREATE TABLE ACCOUNT (
	ID INTEGER NOT NULL AUTO_INCREMENT,
	IDENTIFIER VARCHAR(80) NOT NULL,
	NAME VARCHAR(80) NOT NULL,
	ADMIN BOOLEAN NOT NULL DEFAULT FALSE,
	AUTH_MODE VARCHAR(10) NOT NULL,
	AUTH_CREDENTIALS VARCHAR(400) NULL,
	CONSTRAINT PK_ACCOUNT PRIMARY KEY (ID),
	CONSTRAINT UQ_ACCOUNT_IDENTIFIER UNIQUE (IDENTIFIER),
	CONSTRAINT UQ_ACCOUNT_NAME UNIQUE (NAME)
);

-- Group of projects
CREATE TABLE PROJECT_GROUP (
	ID INTEGER NOT NULL AUTO_INCREMENT,
	NAME VARCHAR(80) NOT NULL,
	DESCRIPTION VARCHAR(1000) NULL,
	CONSTRAINT PK_PROJECTGROUP PRIMARY KEY (ID),
	CONSTRAINT UQ_PROJECTGROUP UNIQUE (NAME)
);

-- Project
CREATE TABLE PROJECT (
	ID INTEGER NOT NULL AUTO_INCREMENT,
	NAME VARCHAR(80) NOT NULL,
	PROJECT_GROUP INTEGER NULL,
	DESCRIPTION VARCHAR(1000) NULL,
	CONSTRAINT PK_PROJECT PRIMARY KEY (ID),
	CONSTRAINT UQ_PROJECT UNIQUE (NAME),
	CONSTRAINT FK_PROJECT_PROJECT_GROUP FOREIGN KEY (PROJECT_GROUP) REFERENCES PROJECT_GROUP (ID) ON DELETE SET NULL
);

-- Branch
CREATE TABLE BRANCH (
	ID INTEGER NOT NULL AUTO_INCREMENT,
	PROJECT INTEGER NOT NULL,
	NAME VARCHAR(80) NOT NULL,
	DESCRIPTION VARCHAR(1000) NULL,
	CONSTRAINT PK_BRANCH PRIMARY KEY (ID),
	CONSTRAINT UQ_BRANCH UNIQUE (PROJECT, NAME),
	CONSTRAINT FK_BRANCH_PROJECT FOREIGN KEY (PROJECT) REFERENCES PROJECT (ID) ON DELETE CASCADE
);

-- Build
CREATE TABLE BUILD (
	ID INTEGER NOT NULL AUTO_INCREMENT,
	BRANCH INTEGER NOT NULL,
	NAME VARCHAR(80) NOT NULL,
	DESCRIPTION VARCHAR(1000) NULL,
	CONSTRAINT PK_BUILD PRIMARY KEY (ID),
	CONSTRAINT UQ_BUILD UNIQUE (BRANCH, NAME),
	CONSTRAINT FK_BUILD_BRANCH FOREIGN KEY (BRANCH) REFERENCES BRANCH (ID) ON DELETE CASCADE
);

-- Promotion level
CREATE TABLE PROMOTION_LEVEL (
	ID INTEGER NOT NULL AUTO_INCREMENT,
	BRANCH INTEGER NOT NULL,
	NAME VARCHAR(80) NOT NULL,
	DESCRIPTION VARCHAR(1000) NULL,
	CONSTRAINT PK_PROMOTION_LEVEL PRIMARY KEY (ID),
	CONSTRAINT UQ_PROMOTION_LEVEL UNIQUE (BRANCH, NAME),
	CONSTRAINT FK_PROMOTION_LEVEL_BRANCH FOREIGN KEY (BRANCH) REFERENCES BRANCH (ID) ON DELETE CASCADE
);

-- Validation stamp
CREATE TABLE VALIDATION_STAMP (
	ID INTEGER NOT NULL AUTO_INCREMENT,
	PROMOTION_LEVEL INTEGER NOT NULL,
	NAME VARCHAR(80) NOT NULL,
	DESCRIPTION VARCHAR(1000) NULL,
	CONSTRAINT PK_VALIDATION_STAMP PRIMARY KEY (ID),
	CONSTRAINT UQ_VALIDATION_STAMP UNIQUE (PROMOTION_LEVEL, NAME),
	CONSTRAINT FK_VALIDATION_STAMP_PROMOTION_LEVEL FOREIGN KEY (PROMOTION_LEVEL) REFERENCES PROMOTION_LEVEL (ID) ON DELETE CASCADE
);

-- Validation run
CREATE TABLE VALIDATION_RUN (
	ID INTEGER NOT NULL AUTO_INCREMENT,
	VALIDATION_STAMP INTEGER NOT NULL,
	BUILD INTEGER NOT NULL,
	DESCRIPTION VARCHAR(1000) NULL,
	STATUS VARCHAR(20) NOT NULL,
	CONSTRAINT PK_VALIDATION_RUN PRIMARY KEY (ID),
	CONSTRAINT FK_VALIDATION_RUN_VALIDATION_STAMP FOREIGN KEY (VALIDATION_STAMP) REFERENCES VALIDATION_STAMP (ID) ON DELETE CASCADE,
	CONSTRAINT FK_VALIDATION_RUN_BUILD FOREIGN KEY (BUILD) REFERENCES BUILD (ID) ON DELETE CASCADE
);

-- Comments
CREATE TABLE COMMENT (
	ID INTEGER NOT NULL AUTO_INCREMENT,
	CONTENT VARCHAR(1000) NOT NULL,
	CONSTRAINT PK_COMMENT PRIMARY KEY (ID),
	PROJECT_GROUP INTEGER NULL,
	PROJECT INTEGER NULL,
	BRANCH INTEGER NULL,
	BUILD INTEGER NULL,
	PROMOTION_LEVEL INTEGER NULL,
	VALIDATION_STAMP INTEGER NULL,
	VALIDATION_RUN INTEGER NULL,
	CONSTRAINT FK_COMMENT_PROJECT_GROUP FOREIGN KEY (PROJECT_GROUP) REFERENCES PROJECT_GROUP (ID) ON DELETE CASCADE,
	CONSTRAINT FK_COMMENT_PROJECT FOREIGN KEY (PROJECT) REFERENCES PROJECT (ID) ON DELETE CASCADE,
	CONSTRAINT FK_COMMENT_BRANCH FOREIGN KEY (BRANCH) REFERENCES BRANCH (ID) ON DELETE CASCADE,
	CONSTRAINT FK_COMMENT_BUILD FOREIGN KEY (BUILD) REFERENCES BUILD (ID) ON DELETE CASCADE,
	CONSTRAINT FK_COMMENT_PROMOTION_LEVEL FOREIGN KEY (PROMOTION_LEVEL) REFERENCES PROMOTION_LEVEL (ID) ON DELETE CASCADE,
	CONSTRAINT FK_COMMENT_VALIDATION_STAMP FOREIGN KEY (VALIDATION_STAMP) REFERENCES VALIDATION_STAMP (ID) ON DELETE CASCADE,
	CONSTRAINT FK_COMMENT_VALIDATION_RUN FOREIGN KEY (VALIDATION_RUN) REFERENCES VALIDATION_RUN (ID) ON DELETE CASCADE
);

-- Properties
CREATE TABLE PROPERTIES (
	ID INTEGER NOT NULL AUTO_INCREMENT,
	NAME VARCHAR(20) NOT NULL,
	VALUE VARCHAR(300) NOT NULL,
	CONSTRAINT PK_PROPERTIES PRIMARY KEY (ID),
	PROJECT_GROUP INTEGER NULL,
	PROJECT INTEGER NULL,
	BRANCH INTEGER NULL,
	BUILD INTEGER NULL,
	PROMOTION_LEVEL INTEGER NULL,
	VALIDATION_STAMP INTEGER NULL,
	VALIDATION_RUN INTEGER NULL,
	CONSTRAINT FK_PROPERTIES_PROJECT_GROUP FOREIGN KEY (PROJECT_GROUP) REFERENCES PROJECT_GROUP (ID) ON DELETE CASCADE,
	CONSTRAINT FK_PROPERTIES_PROJECT FOREIGN KEY (PROJECT) REFERENCES PROJECT (ID) ON DELETE CASCADE,
	CONSTRAINT FK_PROPERTIES_BRANCH FOREIGN KEY (BRANCH) REFERENCES BRANCH (ID) ON DELETE CASCADE,
	CONSTRAINT FK_PROPERTIES_BUILD FOREIGN KEY (BUILD) REFERENCES BUILD (ID) ON DELETE CASCADE,
	CONSTRAINT FK_PROPERTIES_PROMOTION_LEVEL FOREIGN KEY (PROMOTION_LEVEL) REFERENCES PROMOTION_LEVEL (ID) ON DELETE CASCADE,
	CONSTRAINT FK_PROPERTIES_VALIDATION_STAMP FOREIGN KEY (VALIDATION_STAMP) REFERENCES VALIDATION_STAMP (ID) ON DELETE CASCADE,
	CONSTRAINT FK_PROPERTIES_VALIDATION_RUN FOREIGN KEY (VALIDATION_RUN) REFERENCES VALIDATION_RUN (ID) ON DELETE CASCADE
);

-- Audit
CREATE TABLE AUDIT (
	ID INTEGER NOT NULL AUTO_INCREMENT,
	AUTHOR VARCHAR(80) NOT NULL,
	AUTHOR_ID INTEGER NULL,
	AUDIT_TIMESTAMP TIMESTAMP NOT NULL,
	AUDIT_CREATION BOOLEAN NOT NULL,
	CONSTRAINT PK_AUDIT PRIMARY KEY (ID),
	CONSTRAINT FK_AUDIT_AUTHOR_ID FOREIGN KEY (AUTHOR_ID) REFERENCES ACCOUNT (ID) ON DELETE SET NULL,
	ACCOUNT INTEGER NULL,
	PROJECT_GROUP INTEGER NULL,
	PROJECT INTEGER NULL,
	BRANCH INTEGER NULL,
	BUILD INTEGER NULL,
	PROMOTION_LEVEL INTEGER NULL,
	VALIDATION_STAMP INTEGER NULL,
	VALIDATION_RUN INTEGER NULL,
	COMMENT INTEGER NULL,
	CONSTRAINT FK_AUDIT_ACCOUNT FOREIGN KEY (ACCOUNT) REFERENCES ACCOUNT (ID) ON DELETE CASCADE,
	CONSTRAINT FK_AUDIT_PROJECT_GROUP FOREIGN KEY (PROJECT_GROUP) REFERENCES PROJECT_GROUP (ID) ON DELETE CASCADE,
	CONSTRAINT FK_AUDIT_PROJECT FOREIGN KEY (PROJECT) REFERENCES PROJECT (ID) ON DELETE CASCADE,
	CONSTRAINT FK_AUDIT_BRANCH FOREIGN KEY (BRANCH) REFERENCES BRANCH (ID) ON DELETE CASCADE,
	CONSTRAINT FK_AUDIT_BUILD FOREIGN KEY (BUILD) REFERENCES BUILD (ID) ON DELETE CASCADE,
	CONSTRAINT FK_AUDIT_PROMOTION_LEVEL FOREIGN KEY (PROMOTION_LEVEL) REFERENCES PROMOTION_LEVEL (ID) ON DELETE CASCADE,
	CONSTRAINT FK_AUDIT_VALIDATION_STAMP FOREIGN KEY (VALIDATION_STAMP) REFERENCES VALIDATION_STAMP (ID) ON DELETE CASCADE,
	CONSTRAINT FK_AUDIT_VALIDATION_RUN FOREIGN KEY (VALIDATION_RUN) REFERENCES VALIDATION_RUN (ID) ON DELETE CASCADE,
	CONSTRAINT FK_AUDIT_COMMENT FOREIGN KEY (COMMENT) REFERENCES COMMENT (ID) ON DELETE CASCADE
);