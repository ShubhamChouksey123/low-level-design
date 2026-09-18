# Stock Trading and Matching System 


## Actors
- buyers
- sellers


## Functional Requirements
- Buyer should be able to add a buy order with an bid price 
- Seller should be able to add an order with an ask price
- A trade should be settled in a best value, and should settle with the best available order based on price-time priority

## Out of Scope
- cancel an order
- payment/ reserve the funds in case of buy order
- live price provider


## Entity and their Responsibility

- Stock
- Order
- OrderType Enum
- OrderBook
- Trade
- MatchingEngine
- PricingStrategy
- RestingOrderPriceStrategy
- 

