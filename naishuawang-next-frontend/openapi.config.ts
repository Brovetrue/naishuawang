// @ts-ignore
const { generateService } = require("@umijs/openapi");

generateService({
    requestLibPath: "import request from '@/libs/request'",
    schemaPath: "http://47.97.24.232:6061/api/v2/api-docs",
    serversPath: "./src",
});