# Jackson Implicit Polymorphic Type Handling (PTH)

## Usage

```java
@JsonImplicitTypes
@JsonSubTypes({
    @JsonSubTypes.Type(value = FixedReward.class),
    @JsonSubTypes.Type(value = VariableReward.class)})
interface Reward { }

class FixedReward implements Reward {
    public int value;
}

class VariableReward implements Reward {
    public int min, max;
}

void example() {
    ObjectMapper mapper = new ObjectMapper().registerModule(new ImplicitPolymorphismTypeHandleModule());
    
    ArrayList<Reward> rewards = mapper.readValue(
        "[{\"value\":40},{\"min\":35,\"max\":45}]", 
        new TypeReference<ArrayList<Reward>>() {});

    System.out.println(rewards.toString()); // [FixedReward@1, VariableReward@2]
}
```


## Motivation
JSON polymorphic type handling in strongly-typed runtime environments (such as JVM) is usually done by adding a type name as a meta field (also known as _discriminator_).

```java
@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, property="@type")
interface Reward {}

class FixedReward implements Reward {
    public int value;
}
// Serialized as: {"@type": "FixedReward", "value": 40}

class VariableReward implements Reward {
    public int min, max;
}
// Serialized as: {"@type": "VariableReward", "min": 10, "max": 70}
``` 

Meanwhile in dynamic-typed runtime environments a concrete type usually inferred by existence of the properties.
Types above can be modeled in TypeScript as a union type:

```typescript
type FixedReward = {value: Number}
type VariableReward = {min: Number, max: Number}
type Reward = FixedReward | VariableReward

const r1: Reward = {min: 0, max: 1};
const r2: Reward = {value: 1};
```

The aim of the package is to allow interoperability in heterogeneous systems for discriminated union types without explicit discriminator.

## Favor explicit type handling

> Resort to implicit type handling only if for whatever reason you can't change the JSON contract.

Having an explicit discriminator is considered a better practice, as it allows to validate and infer type much simpler across all type of runtime environments.

In OpenAPI schema definition for it's possible to define discriminator property to help clients infer an object type (see __§Discriminator__ in [Inheritance and Polymorphysm](https://swagger.io/docs/specification/data-models/inheritance-and-polymorphism/)).

It allows a code generation tools (such as [openapi-generator](https://github.com/OpenAPITools/openapi-generator)) to better separate types. 
There are messages already claiming support of inheritance without `discriminator.propertyName` won't be supported for Java ([issue](https://github.com/OpenAPITools/openapi-generator/issues/5097)).

It's considered good practice in TypeScript to use tagged (or discriminated) unions for custom union types. 
It makes type guards a lot simpler.
See more in [TypeScript Deep Dive](https://basarat.gitbook.io/typescript/type-system/discriminated-unions).

So example above is better to be modeled as:

##### Java

```java
@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, property="type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = FixedReward.class, name = "fixed"),
    @JsonSubTypes.Type(value = VariableReward.class, name = "variable")
})
interface Reward {}

class FixedReward implements Reward {
    public int value;
}
// Serialized as: {"type": "fixed", "value": 40}

class VariableReward implements Reward {
    public int min, max;
}
// Serialized as: {"type": "variable", "min": 10, "max": 70}
``` 

##### TypeScript

```typescript
type FixedReward = {type: "fixed", value: Number}
type VariableReward = {type: "variable", min: Number, max: Number}
type Reward = FixedReward | VariableReward
```