export enum OrderStatus {
  PENDING = "PENDING", // Order placed, waiting for restaurant confirmation
  ACCEPTED = "ACCEPTED", // Restaurant accepted the order
  REJECTED = "REJECTED", // Restaurant rejected the order
  PREPARING = "PREPARING", // Order is being prepared
  READY_FOR_PICKUP = "READY_FOR_PICKUP", // Order is ready for customer pickup
  DRIVER_ASSIGNED = "DRIVER_ASSIGNED", // Driver has been assigned to the order
  IN_TRANSIT = "IN_TRANSIT", // Order is in transit to the customer
  DELIVERED = "DELIVERED", // Order successfully delivered to the customer
  CANCELLED = "CANCELLED", // Order cancelled by the user
}
