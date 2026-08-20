---


---

1. Create an Inventory REST API with a new InventoryController, its service and persistence layers.
2. Inventory GET methods are always having an productId filter parameter.
3. Add a DELETE endpoint filtered by Inventory Id for rolling back failed transactions.
4. The persistence strategy for the inventory must follow the Event Source pattern.
5. After a new Inventory row is persisted, a new InventoryEvent must be produced. 
6. The InventoryEvent holds the InventoryDTO new data as payload.