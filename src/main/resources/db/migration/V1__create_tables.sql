CREATE TABLE usuario(
    id UUID PRIMARY KEY,
    login VARCHAR(20) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(64) NOT NULL,
    roles VARCHAR(20)
);

CREATE TABLE categoria(
    id UUID PRIMARY KEY,
    nome_categoria VARCHAR(255) NOT NULL,
    usuario_id UUID NOT NULL,
    FOREIGN KEY (usuario_id) REFERENCES usuario(id)
);

CREATE TABLE registro(
    id UUID PRIMARY KEY,
    data DATE NOT NULL,
    tempo_em_minutos INTEGER NOT NULL,
    resumo VARCHAR(2000),
    planejamento VARCHAR(2000),
    usuario_id UUID NOT NULL,
    FOREIGN KEY (usuario_id) REFERENCES usuario(id)
);

CREATE TABLE registro_categorias(
    registro_id UUID NOT NULL,
    categoria_id UUID NOT NULL,
    PRIMARY KEY (registro_id, categoria_id),
    FOREIGN KEY (registro_id) REFERENCES registro(id) ON DELETE CASCADE,
    FOREIGN KEY (categoria_id) REFERENCES categoria(id) ON DELETE CASCADE
);

CREATE TABLE refresh_token(
    id UUID PRIMARY KEY,
    usuario_id UUID NOT NULL,
    token_hash VARCHAR(255) NOT NULL,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    sessao_expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    revogado BOOLEAN NOT NULL
);