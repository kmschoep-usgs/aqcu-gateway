# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html). (Patch version X.Y.0 is implied if not specified.)

## [Unreleased]

## [0.2.0] 2020-11-10
## Added
- Endpoint to retrieve authenticated user and role information

## Changed
- Manage artifacts in WMA Artifactory instead of WSI Artifactory
- Make lambda timeout length configurable
- Enable potential for up to 450 locations to be configured for a groundwater level extremes report and groundwater visit review status report
- Dependency upgrades for security fixes

### Removed
- Dependency on travis.ci

## [0.1.0] - 2018-12-19
## Added
- aqcu-lookups S3 services
- Groundwater level extremes report lambda service
- Groundwater level visit review status report lambda service
- Add code-quality library to pom

## Changed
- Merge docker and code repositories
- Upgrade to Spring Boot 2
- Fix Critical and High vulnerabilities
- Fix CORS to work with Spring Boot 2

## Removed
- Hystrix
- Openfeign

## [0.0.7] - 2018-12-19
## Added
- Refactored Extremes report service
- Refactored Site Visit Peak report service
- Refactored V-Diagram report service
- Refactored UV Hydrograph report service
- Refactored 5-year Hydrograph report service

## Removed
- TLS 1.0/1.1

## [0.0.6] - 2018-09-13
### Added
- Refactored Derivation Chain report service
- Refactored Corrections at a Glance service

## [0.0.5] - 2018-08-31
## Changed
- CORS to work with Zuul

## [0.0.4] - 2018-07-13
## Added
- Enable forwarding of sensitive headers

## Changed
- Make login URL configurable
- Migrate to Water Auth for authentication from CIDA Auth.

## [0.0.3] - 2018-06-15
## Added
- Lookup service (aqcu-lookups)

## [0.0.2] - 2018-03-08
### Added
- Refactored DV Hydrograph report service

### Changed
- Update routing to point to old and new report services

### Removed
- Hard-coded 1 minute report timeout configuration

## [0.0.1] - 2018-04-19
### Added
- Initial release - happy path.


[Unreleased]: https://github.com/USGS-CIDA/aqcu-gateway/compare/master...master
[0.2.0]: https://github.com/USGS-CIDA/aqcu-lookups/compare/0.1.0...0.2.0
[0.1.0]: https://github.com/USGS-CIDA/aqcu-lookups/compare/aqcu-gateway-0.0.7...0.1.0
[0.0.7]: https://github.com/USGS-CIDA/aqcu-lookups/compare/aqcu-gateway-0.0.6...aqcu-gateway-0.0.7
[0.0.6]: https://github.com/USGS-CIDA/aqcu-lookups/compare/aqcu-gateway-0.0.5...aqcu-gateway-0.0.6
[0.0.5]: https://github.com/USGS-CIDA/aqcu-lookups/compare/aqcu-gateway-0.0.4...aqcu-gateway-0.0.5
[0.0.4]: https://github.com/USGS-CIDA/aqcu-lookups/compare/aqcu-gateway-0.0.3...aqcu-gateway-0.0.4
[0.0.3]: https://github.com/USGS-CIDA/aqcu-lookups/compare/aqcu-gateway-0.0.2...aqcu-gateway-0.0.3
[0.0.2]: https://github.com/USGS-CIDA/aqcu-lookups/compare/aqcu-gateway-0.0.1...aqcu-gateway-0.0.2