# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added

### Changed

### Fixed

## [1.3.1]

### Fixed
* some fixes

## [1.3.0]

### Added
* add custom contentTypeHandler to handle wrong/empty contentTypes

### Changed
* update to java 21
* update some libraries

## [1.2.3]

### Fixed
* handel missing to filed in mime massage

## [1.2.2]

### Fixed
* remove useless Runtime Exception throw

## [1.2.1]

### Fixed
* MailSaveAttachmentFile: prevent disposition is null & check if baseDir exist  

## [1.2.0]

### Added
* msgID FetchPop3 info object

## [1.1.0]

### Added
- filter by POP3 UIDs on MailPop3GetMessages

### Changed
- rename MailGetMessages to MailPop3GetMessages

## [1.0.3]

### Fixed
- remove try/catch in MailSendMessage to prevent an exception tree (RuntimeException)

## [1.0.1]

### Fixed
- prevent user & password from empty string

## [1.0.0]

### Added
- initial startup
