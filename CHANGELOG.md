# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added

### Changed

### Fixed

## [1.7.6]

### Fixed
* handle Message-ID ist null when fetching messages

## [1.7.5]

### Fixed
* better handle null value on Header: subject and from

## [1.7.4]

### Fixed
* by sure to send UTF-8 a creation Text MimeBodyPart 

## [1.7.3]

### Fixed
* fix MailModTextBody to handle nested multipart correctly

## [1.7.2]

### Fixed
* NPE: debug log on MailGetPop3Messages

## [1.7.1]

### Fixed
* NPE: debug log on MailFetchPop3Message

## [1.7.0]

### Added
* pop3fetchInfo: add a list of all headers to MessageHeadInfo

## [1.6.3]

### Fixed
* handling break getting messages on pop3 client timeout exceptions is not working when the pop3 server is cutting the connection

## [1.6.2]

### Fixed
* debug log on MailFetchPop3Message, all Headers are now logged

## [1.6.1]

### Fixed
* fix a complete break on getting pop3 messages (MailPop3GetMessages)

## [1.6.0]

### Added
* logging on SetHeader atomic

## [1.5.1]

### Fixed
* fix remove header with null value in SetHeader atomic

## [1.5.0]

### Added

* add MailRemovePersonalFromRecipients atomic for easy remove personal from recipients

## [1.4.1]

### Fixed
* fix check connection, transport & store handling

## [1.4.0]

### Added

* add ConnectionCheck atomic for easy check if connection based on properties and credentials

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
