require('dotenv').config();

const config = {
    user: process.env.DB_USER,
    password: process.env.DB_PWD,
    server: process.env.DB_SERVER,
    database: process.env.DB_NAME,
    options: {
        trustServerCertificate: true, // Dev local cần cái này
        encrypt: false,
        enableArithAbort: true
    },
    port: 1433
};

module.exports = config;