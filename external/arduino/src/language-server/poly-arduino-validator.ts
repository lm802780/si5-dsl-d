import { ValidationAcceptor, ValidationChecks, ValidationRegistry } from 'langium';
import { PolyArduinoAstType, Person } from './generated/ast';
import type { PolyArduinoServices } from './poly-arduino-module';

/**
 * Registry for validation checks.
 */
export class PolyArduinoValidationRegistry extends ValidationRegistry {
    constructor(services: PolyArduinoServices) {
        super(services);
        const validator = services.validation.PolyArduinoValidator;
        const checks: ValidationChecks<PolyArduinoAstType> = {
            Person: validator.checkPersonStartsWithCapital
        };
        this.register(checks, validator);
    }
}

/**
 * Implementation of custom validations.
 */
export class PolyArduinoValidator {

    checkPersonStartsWithCapital(person: Person, accept: ValidationAcceptor): void {
        if (person.name) {
            const firstChar = person.name.substring(0, 1);
            if (firstChar.toUpperCase() !== firstChar) {
                accept('warning', 'Person name should start with a capital.', { node: person, property: 'name' });
            }
        }
    }

}
