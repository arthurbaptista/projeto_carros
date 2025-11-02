CREATE DATABASE IF NOT EXISTS locadora_veiculos;
USE locadora_veiculos;

CREATE TABLE IF NOT EXISTS clientes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    sobrenome VARCHAR(100) NOT NULL,
    rg VARCHAR(20) UNIQUE NOT NULL,
    cpf VARCHAR(14) UNIQUE NOT NULL,
    endereco TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS veiculos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tipo VARCHAR(20) NOT NULL, -- 'AUTOMOVEL', 'MOTOCICLETA', 'VAN'
    marca VARCHAR(50) NOT NULL,
    estado VARCHAR(20) NOT NULL,
    categoria VARCHAR(20) NOT NULL,
    valor_compra DECIMAL(10,2) NOT NULL,
    placa VARCHAR(10) UNIQUE NOT NULL,
    ano INT NOT NULL,
    modelo VARCHAR(50) NOT NULL
);

-- NOVA TABELA
CREATE TABLE IF NOT EXISTS locacoes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    veiculo_placa VARCHAR(10) NOT NULL,
    cliente_cpf VARCHAR(14) NOT NULL,
    data_locacao DATE NOT NULL,
    dias INT NOT NULL,
    valor_total DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (veiculo_placa) REFERENCES veiculos(placa),
    FOREIGN KEY (cliente_cpf) REFERENCES clientes(cpf)
);