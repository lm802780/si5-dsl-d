/******************************************************************************
 * This file was generated by langium-cli 0.5.0.
 * DO NOT EDIT MANUALLY!
 ******************************************************************************/

/* eslint-disable @typescript-eslint/array-type */
/* eslint-disable @typescript-eslint/no-empty-interface */
import { AstNode, AstReflection, Reference, ReferenceInfo, isAstNode, TypeMetaData } from 'langium';

export interface Greeting extends AstNode {
    readonly $container: Model;
    person: Reference<Person>
}

export const Greeting = 'Greeting';

export function isGreeting(item: unknown): item is Greeting {
    return reflection.isInstance(item, Greeting);
}

export interface Model extends AstNode {
    greetings: Array<Greeting>
    persons: Array<Person>
}

export const Model = 'Model';

export function isModel(item: unknown): item is Model {
    return reflection.isInstance(item, Model);
}

export interface Person extends AstNode {
    readonly $container: Model;
    name: string
}

export const Person = 'Person';

export function isPerson(item: unknown): item is Person {
    return reflection.isInstance(item, Person);
}

export type PolyArduinoAstType = 'Greeting' | 'Model' | 'Person';

export class PolyArduinoAstReflection implements AstReflection {

    getAllTypes(): string[] {
        return ['Greeting', 'Model', 'Person'];
    }

    isInstance(node: unknown, type: string): boolean {
        return isAstNode(node) && this.isSubtype(node.$type, type);
    }

    isSubtype(subtype: string, supertype: string): boolean {
        if (subtype === supertype) {
            return true;
        }
        switch (subtype) {
            default: {
                return false;
            }
        }
    }

    getReferenceType(refInfo: ReferenceInfo): string {
        const referenceId = `${refInfo.container.$type}:${refInfo.property}`;
        switch (referenceId) {
            case 'Greeting:person': {
                return Person;
            }
            default: {
                throw new Error(`${referenceId} is not a valid reference id.`);
            }
        }
    }

    getTypeMetaData(type: string): TypeMetaData {
        switch (type) {
            case 'Model': {
                return {
                    name: 'Model',
                    mandatory: [
                        { name: 'greetings', type: 'array' },
                        { name: 'persons', type: 'array' }
                    ]
                };
            }
            default: {
                return {
                    name: type,
                    mandatory: []
                };
            }
        }
    }
}

export const reflection = new PolyArduinoAstReflection();
