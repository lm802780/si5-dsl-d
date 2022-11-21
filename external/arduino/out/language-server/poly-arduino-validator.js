"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.PolyArduinoValidator = exports.PolyArduinoValidationRegistry = void 0;
const langium_1 = require("langium");
/**
 * Registry for validation checks.
 */
class PolyArduinoValidationRegistry extends langium_1.ValidationRegistry {
    constructor(services) {
        super(services);
        const validator = services.validation.PolyArduinoValidator;
        const checks = {
            Person: validator.checkPersonStartsWithCapital
        };
        this.register(checks, validator);
    }
}
exports.PolyArduinoValidationRegistry = PolyArduinoValidationRegistry;
/**
 * Implementation of custom validations.
 */
class PolyArduinoValidator {
    checkPersonStartsWithCapital(person, accept) {
        if (person.name) {
            const firstChar = person.name.substring(0, 1);
            if (firstChar.toUpperCase() !== firstChar) {
                accept('warning', 'Person name should start with a capital.', { node: person, property: 'name' });
            }
        }
    }
}
exports.PolyArduinoValidator = PolyArduinoValidator;
//# sourceMappingURL=poly-arduino-validator.js.map