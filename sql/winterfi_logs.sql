SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

-- Database Name: `winterfi_logs`

-- Table structure for table `bands`
CREATE TABLE `bands` (
                         `id` int(11) NOT NULL,
                         `name` varchar(11) NOT NULL,
                         PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Insert data for table `bands`
INSERT INTO `bands` (`id`, `name`) VALUES
(0, 'ALL'),
(1, '160M'),
(2, '80M'),
(3, '40M'),
(4, '20M'),
(5, '15M'),
(6, '10M'),
(7, '6M'),
(8, '4M'),
(9, '2M'),
(10, '222'),
(11, '432'),
(12, '902'),
(13, '1.2G'),
(14, '2.3G'),
(15, '3.4G'),
(16, '5.7G'),
(17, '10G'),
(18, '24G'),
(19, '47G'),
(20, '75G'),
(21, '123G'),
(22, '134G'),
(23, '241G'),
(24, 'Light'),
(25, 'VHF-3-BAND'),
(26, 'VHF-FM-ONLY');

-- Table structure for table `assisted`
CREATE TABLE `assisted` (
                            `id` int(11) NOT NULL,
                            `name` varchar(12) NOT NULL,
                            PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Insert data for table `assisted`
INSERT INTO `assisted` (`id`, `name`) VALUES
(1, 'ASSISTED'),
(2, 'NON-ASSISTED');

-- Table structure for table `modes`
CREATE TABLE `modes` (
                         `id` int(11) NOT NULL,
                         `name` varchar(10) NOT NULL,
                         PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Insert data for table `modes`
INSERT INTO `modes` (`id`, `name`) VALUES
(1, 'CW'),
(2, 'DIGI'),
(3, 'FM'),
(4, 'RTTY'),
(5, 'SSB'),
(6, 'MIXED'),
(0, 'N/A');

-- Table structure for table `qso_modes`
CREATE TABLE `qso_modes` (
                             `id` int(11) NOT NULL,
                             `name` varchar(2) NOT NULL,
                             PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Insert data for table `modes`
INSERT INTO `qso_modes` (`id`, `name`) VALUES
(1, 'CW'),
(2, 'PH'),
(3, 'FM'),
(4, 'RY'),
(5, 'DG');

-- Table structure for table `operator_types`
CREATE TABLE `operator_types` (
                                  `id` int(11) NOT NULL,
                                  `name` varchar(9) NOT NULL,
                                  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Dumping data for table `operator_types`
INSERT INTO `operator_types` (`id`, `name`) VALUES
(1, 'SINGLE-OP'),
(2, 'MULTI-OP'),
(3, 'CHECKLOG'),
(0, 'N/A');

-- Table structure for table `powers`
CREATE TABLE `powers` (
                          `id` int(11) NOT NULL,
                          `name` varchar(4) NOT NULL,
                          PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Insert data for table `powers`
INSERT INTO `powers` (`id`, `name`) VALUES
(1, 'HIGH'),
(2, 'LOW'),
(3, 'QRP'),
(0, 'ALL');

-- Table structure for table `stations`
CREATE TABLE `stations` (
                            `id` int(11) NOT NULL,
                            `name` varchar(15) NOT NULL,
                            PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Dumping data for table `stations`
INSERT INTO `stations` (`id`, `name`) VALUES
(1, 'FIXED'),
(2, 'MOBILE'),
(3, 'PORTABLE'),
(4, 'ROVER'),
(5, 'ROVER-LIMITED'),
(6, 'ROVER-UNLIMITED'),
(7, 'EXPEDITION'),
(8, 'HQ'),
(9, 'SCHOOL'),
(0, 'N/A');

-- Table structure for table `times`
CREATE TABLE `times` (
                         `id` int(11) NOT NULL,
                         `name` varchar(8) NOT NULL,
                         PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Insert data for table `times`
INSERT INTO `times` (`id`, `name`) VALUES
(1, '6-HOURS'),
(2, '12-HOURS'),
(3, '24-HOURS'),
(0, 'N/A');

-- Table structure for table `transmitters`
CREATE TABLE `transmitters` (
                                `id` int(11) NOT NULL,
                                `name` varchar(9) NOT NULL,
                                PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Insert data for table `transmitters`
INSERT INTO `transmitters` (`id`, `name`) VALUES
(1, 'ONE'),
(2, 'TWO'),
(3, 'LIMITED'),
(4, 'UNLIMITED'),
(5, 'SWL'),
(0, 'N/A');

-- Table structure for table `overlays`
CREATE TABLE `overlays` (
                            `id` int(11) NOT NULL,
                            `name` varchar(11) NOT NULL,
                            PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Insert data for table `overlays`
INSERT INTO `overlays` (`id`, `name`) VALUES
(1, 'CLASSIC'),
(2, 'ROOKIE'),
(3, 'TB-WIRES'),
(4, 'NOVICE-TECH'),
(5, 'OVER-50'),
(0, 'N/A');

-- Table structure for table `arrl_sections`
CREATE TABLE `arrl_sections` (
                                 `abbrev` varchar(20) NOT NULL,
                                 `name` varchar(30) NOT NULL,
                                 `description` varchar(255) NOT NULL
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;

-- Insert data for table `arrl_sections`
INSERT INTO `arrl_sections` (`abbrev`, `name`, `description`) VALUES
('CT', 'Connecticut', 'Call Sign Area 1'),
('EMA', 'Eastern  Massachusetts', 'Call Sign Area 1'),
('ME', 'Maine', 'Call Sign Area 1'),
('NH', 'New Hampshire', 'Call Sign Area 1'),
('RI', 'Rhode Island', 'Call Sign Area 1'),
('VT', 'Vermont', 'Call Sign Area 1'),
('WMA', 'Western Massachusetts', 'Call Sign Area 1'),
('ENY', 'Eastern New York', 'Call Sign Area 2'),
('NLI', 'New York City - Long Island', 'Call Sign Area 2'),
('NNJ', 'Northern New Jersey', 'Call Sign Area 2'),
('NNY', 'Northern New York', 'Call Sign Area 2'),
('SNJ', 'Southern New Jersey', 'Call Sign Area 2'),
('WNY', 'Western New York', 'Call Sign Area 2'),
('DE', 'Delaware', 'Call Sign Area 3'),
('EPA', 'Eastern Pennsylvania', 'Call Sign Area 3'),
('MDC', 'Maryland-DC', 'Call Sign Area 3'),
('WPA', 'Western Pennsylvania', 'Call Sign Area 3'),
('AL', 'Alabama', 'Call Sign Area 4'),
('GA', 'Georgia', 'Call Sign Area 4'),
('KY', 'Kentucky', 'Call Sign Area 4'),
('NC', 'North Carolina', 'Call Sign Area 4'),
('NFL', 'Northern Florida', 'Call Sign Area 4'),
('SC', 'South Carolina', 'Call Sign Area 4'),
('SFL', 'Southern Florida', 'Call Sign Area 4'),
('WCF', 'West  Central Florida', 'Call Sign Area 4'),
('TN', 'Tennessee', 'Call Sign Area 4'),
('VA', 'Virginia', 'Call Sign Area 4'),
('PR', 'Puerto Rico', 'Call Sign Area 4'),
('VI', 'Virgin Islands', 'Call Sign Area 4'),
('AR', 'Arkansas', 'Call Sign Area 5'),
('LA', 'Louisiana', 'Call Sign Area 5'),
('MS', 'Mississippi', 'Call Sign Area 5'),
('NM', 'New Mexico', 'Call Sign Area 5'),
('NTX', 'North Texas', 'Call Sign Area 5'),
('OK', 'Oklahoma', 'Call Sign Area 5'),
('STX', 'South Texas', 'Call Sign Area 5'),
('WTX', 'West Texas', 'Call Sign Area 5'),
('EB', 'East Bay', 'Call Sign Area 6'),
('LAX', 'Los Angeles', 'Call Sign Area 6'),
('ORG', 'Orange', 'Call Sign Area 6'),
('SB', 'Santa Barbara', 'Call Sign Area 6'),
('SCV', 'Santa Clara Valley', 'Call Sign Area 6'),
('SDG', 'San Diego', 'Call Sign Area 6'),
('SF', 'San Francisco', 'Call Sign Area 6'),
('SJV', 'San Joaquin Valley', 'Call Sign Area 6'),
('SV', 'Sacramento Valley', 'Call Sign Area 6'),
('PAC', 'Pacific', 'Call Sign Area 6'),
('AZ', 'Arizona', 'Call Sign Area 7'),
('EWA', 'Eastern Washington', 'Call Sign Area 7'),
('ID', 'Idaho', 'Call Sign Area 7'),
('MT', 'Montana', 'Call Sign Area 7'),
('NV', 'Nevada', 'Call Sign Area 7'),
('OR', 'Oregon', 'Call Sign Area 7'),
('UT', 'Utah', 'Call Sign Area 7'),
('WWA', 'Western Washington', 'Call Sign Area 7'),
('WY', 'Wyoming', 'Call Sign Area 7'),
('AK', 'Alaska', 'Call Sign Area 7'),
('MI', 'Michigan', 'Call Sign Area 8'),
('OH', 'Ohio', 'Call Sign Area 8'),
('WV', 'West Virginia', 'Call Sign Area 8'),
('IL', 'Illinois', 'Call Sign Area 9'),
('IN', 'Indiana', 'Call Sign Area 9'),
('WI', 'Wisconsin', 'Call Sign Area 9'),
('CO', 'Colorado', 'Call Sign Area 0'),
('IA', 'Iowa', 'Call Sign Area 0'),
('KS', 'Kansas', 'Call Sign Area 0'),
('MN', 'Minnesota', 'Call Sign Area 0'),
('MO', 'Missouri', 'Call Sign Area 0'),
('NE', 'Nebraska', 'Call Sign Area 0'),
('ND', 'North Dakota', 'Call Sign Area 0'),
('SD', 'South Dakota', 'Call Sign Area 0'),
('MAR', 'Maritime', 'Canadian Area Call Sign'),
('NL', 'Newfoundland/Labrador', 'Canadian Area Call Sign'),
('QC', 'Quebec', 'Canadian Area Call Sign'),
('ONE', 'Ontario East', 'Canadian Area Call Sign'),
('ONN', 'Ontario North', 'Canadian Area Call Sign'),
('ONS', 'Ontario South', 'Canadian Area Call Sign'),
('GTA', 'Greater Toronto Area', 'Canadian Area Call Sign'),
('MB', 'Manitoba', 'Canadian Area Call Sign'),
('SK', 'Saskatchewan', 'Canadian Area Call Sign'),
('AB', 'Alberta', 'Canadian Area Call Sign'),
('BC', 'British Columbia', 'Canadian Area Call Sign'),
('NT', 'Northern Territories', 'Canadian Area Call Sign');

-- Table structure for table `soapboxes`
CREATE TABLE `soapboxes` (
                             `id` int(11) NOT NULL AUTO_INCREMENT,
                             `entry_id` int(11) NOT NULL,
                             `soapbox` varchar(255) NOT NULL,
                             PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Table structure for table `addresses`
CREATE TABLE `addresses` (
                             `id` int(11) NOT NULL AUTO_INCREMENT,
                             `entry_id` int(11) NOT NULL,
                             `address` varchar(255) NOT NULL,
                             PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Table structure for table `offtime`
CREATE TABLE `offtimes` (
                            `id` int(11) NOT NULL AUTO_INCREMENT,
                            `entry_id` int(11) NOT NULL,
                            `soapbox` varchar(255) NOT NULL,
                            PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Table structure for table `entry`
CREATE TABLE `entries` (
                           `id` int(11) NOT NULL AUTO_INCREMENT,
                           `log_version` int(11) NOT NULL,
                           `callsign` varchar(20) NOT NULL,
                           `contest` varchar(20) NOT NULL,
                           `assisted_id` tinyint(1) NOT NULL,
                           `band_id` int(11) NOT NULL,
                           `mode_id` int(11) NOT NULL,
                           `operators` int(11) NOT NULL,
                           `operator_type_id` int(11) NOT NULL,
                           `power_id` int(11) NOT NULL,
                           `station_id` int(11) NOT NULL,
                           `time_id` int(11) NOT NULL,
                           `transmitter_id` int(11) NOT NULL,
                           `overlay_id` int(11) NOT NULL,
                           `certificate` tinyint(1) NOT NULL,
                           `claimed_score` int(11) NOT NULL,
                           `club` varchar(255) NOT NULL,
                           `created_by` varchar(255) NOT NULL,
                           `email` varchar(255) NOT NULL,
                           `grid_locator` varchar(6) NOT NULL,
                           `location` varchar(25) NOT NULL,
                           `name` varchar(75) NOT NULL,
                           `address` varchar(75) NOT NULL,
                           `city` varchar(75) NOT NULL,
                           `state_province` varchar(75) NOT NULL,
                           `postalcode` varchar(75) NOT NULL,
                           `country` varchar(75) NOT NULL,
                           `arrl_section` varchar(20) NOT NULL, 		-- arrl_section abbrev
                           `category` varchar(10) NOT NULL,			-- WFD category
                           PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Table structure for table `contacts`
CREATE TABLE `contacts` (
                            `id` int(11) NOT NULL AUTO_INCREMENT,
                            `entry_id` int(11) NOT NULL,
                            `freq` varchar(25) NOT NULL,
                            `qso_mode` int(11) NOT NULL,
                            `contact_date` date NOT NULL,
                            `contact_time` time NOT NULL,
                            `callsign` varchar(25) NOT NULL,
                            `exch` varchar(25) NOT NULL,
                            `category` varchar(25) NOT NULL,
                            `sect` varchar(25) NOT NULL,
                            `transmitter` int(11) NOT NULL,
                            PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Foreign keys for tables
ALTER TABLE entries
    ADD CONSTRAINT FK_assisted_id
        FOREIGN KEY (assisted_id) REFERENCES assisted(id);
ALTER TABLE entries
    ADD CONSTRAINT FK_band_id
        FOREIGN KEY (band_id) REFERENCES bands(id);
ALTER TABLE entries
    ADD CONSTRAINT FK_mode_id
        FOREIGN KEY (mode_id) REFERENCES modes(id);
ALTER TABLE entries
    ADD CONSTRAINT FK_operator_type_id
        FOREIGN KEY (operator_type_id) REFERENCES operator_types(id);
ALTER TABLE entries
    ADD CONSTRAINT FK_power_id
        FOREIGN KEY (power_id) REFERENCES powers(id);
ALTER TABLE entries
    ADD CONSTRAINT FK_station_id
        FOREIGN KEY (station_id) REFERENCES stations(id);
ALTER TABLE entries
    ADD CONSTRAINT FK_time_id
        FOREIGN KEY (time_id) REFERENCES times(id);
ALTER TABLE entries
    ADD CONSTRAINT FK_transmitter_id
        FOREIGN KEY (transmitter_id) REFERENCES transmitters(id);
ALTER TABLE entries
    ADD CONSTRAINT FK_overlay_id
        FOREIGN KEY (overlay_id) REFERENCES overlays(id);

ALTER TABLE soapboxes
    ADD CONSTRAINT FK_entry_soapbox
        FOREIGN KEY (entry_id) REFERENCES entries(id);

ALTER TABLE contacts
    ADD CONSTRAINT FK_entry_contact
        FOREIGN KEY (entry_id) REFERENCES entries(id);

ALTER TABLE offtimes
    ADD CONSTRAINT FK_entry_offtime
        FOREIGN KEY (entry_id) REFERENCES entries(id);

COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;