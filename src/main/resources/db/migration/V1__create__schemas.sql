-- create table Role
CREATE SEQUENCE role_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE PUBLIC.role (
    id INTEGER DEFAULT nextval('role_id_seq') PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(200) NOT NULL,
    priority INTEGER NOT NULL
);

-- create table stakeholder
CREATE SEQUENCE stakeholder_id_seq
START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE PUBLIC.stakeholder (
    id INTEGER DEFAULT nextval('stakeholder_id_seq') PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    role_id INTEGER NOT NULL
);

-- create table intellectual_properties

CREATE SEQUENCE intellectual_properties_id_seq
START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE PUBLIC.intellectual_properties (
    id INTEGER DEFAULT nextval('intellectual_properties_id_seq') PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(200) NOT NULL
);

-- create table profit_distribution_contract
CREATE SEQUENCE profit_distribution_contract_id_seq
START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE PUBLIC.profit_distribution_contract (
    id INTEGER DEFAULT nextval('profit_distribution_contract_id_seq') PRIMARY KEY,
    description VARCHAR(200) NOT NULL,
    ip_id INTEGER,
    contract_active_date DATE,
    deleted BOOLEAN NOT NULL DEFAULT false,
    contract_type VARCHAR(50) NOT NULL,
    executor_stakeholder_id INTEGER NOT NULL,
    contract_priority INTEGER
);

-- create table contract participant
CREATE SEQUENCE contract_participant_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

-- Create contract_participant table
CREATE TABLE PUBLIC.contract_participant (
    id INTEGER DEFAULT nextval('contract_participant_id_seq') PRIMARY KEY,
    contract_id INTEGER NOT NULL,
    stakeholder_id INTEGER NOT NULL,
    participant_percentage DECIMAL(5,2),
    is_executor BOOLEAN NOT NULL DEFAULT false
);

-- Create sequence for money_node
CREATE SEQUENCE IF NOT EXISTS money_node_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

-- Create money_node table
CREATE TABLE money_node (
    id BIGINT DEFAULT nextval('money_node_seq') PRIMARY KEY,
    node_type VARCHAR(20) NOT NULL,
    contract_id BIGINT,
    stakeholder_id BIGINT
);


-- Create sequence for money_edge
CREATE SEQUENCE IF NOT EXISTS money_edge_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

-- Create money_edge table
CREATE TABLE money_edge (
    id BIGINT DEFAULT nextval('money_edge_seq') PRIMARY KEY,
    source_node_id BIGINT NOT NULL,
    target_node_id BIGINT NOT NULL,
    percentage DOUBLE PRECISION NOT NULL
);

-- Create sequence for ip_tree
CREATE SEQUENCE IF NOT EXISTS ip_tree_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

-- Create ip_tree table
CREATE TABLE public.ip_tree (
    id BIGINT DEFAULT nextval('ip_tree_seq') PRIMARY KEY,
    intellectual_property_id BIGINT NOT NULL UNIQUE,
    root_contract_node_id BIGINT NOT NULL
);
