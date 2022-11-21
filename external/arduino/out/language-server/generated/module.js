"use strict";
/******************************************************************************
 * This file was generated by langium-cli 0.5.0.
 * DO NOT EDIT MANUALLY!
 ******************************************************************************/
Object.defineProperty(exports, "__esModule", { value: true });
exports.PolyArduinoGeneratedModule = exports.PolyArduinoGeneratedSharedModule = exports.PolyArduinoLanguageMetaData = void 0;
const ast_1 = require("./ast");
const grammar_1 = require("./grammar");
exports.PolyArduinoLanguageMetaData = {
    languageId: 'poly-arduino',
    fileExtensions: ['.arduino'],
    caseInsensitive: false
};
exports.PolyArduinoGeneratedSharedModule = {
    AstReflection: () => new ast_1.PolyArduinoAstReflection()
};
exports.PolyArduinoGeneratedModule = {
    Grammar: () => (0, grammar_1.PolyArduinoGrammar)(),
    LanguageMetaData: () => exports.PolyArduinoLanguageMetaData,
    parser: {}
};
//# sourceMappingURL=module.js.map