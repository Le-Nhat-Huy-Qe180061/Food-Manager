package DAOs;

import DBConnection.DBConnection;
import Models.Cart;
import Models.CartItem;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import Models.Order;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OrderDAO {

    private Connection conn;
    private PreparedStatement ps;
    private ResultSet rs;

    public OrderDAO() {
        conn = DBConnection.getConnection();
    }

    public void insertOrderStatusNotCFYet(Cart cart) {
        try {
            String sql = "INSERT INTO [dbo].[Order] ([cart_id], [customer_id], [order_status_id], [payment_method_id], [contact_phone], [delivery_address], [order_time], [order_total], [order_note], [delivery_time], [order_cancel_time]) VALUES (?, 1, 1, 1, 0, 'NULL', GETDATE(), ?, null, null, null)";
            ps = conn.prepareStatement(sql);
            for (CartItem item : cart.getItems()) {
                ps.setInt(1, item.getFood().getFoodID());
                ps.setDouble(2, cart.getTotalMoney());
                ps.executeUpdate();
            }
        } catch (SQLException ex) {
            Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public List<Order> getAllList() {
        ResultSet orderRS = this.getAll();
        List<Order> orderList = new ArrayList<>();
        try {
            while (orderRS.next()) {
                Order order = new Order(orderRS.getInt("order_id"),
                        orderRS.getInt("cart_id"),
                        orderRS.getInt("customer_id"),
                        orderRS.getByte("order_status_id"),
                        this.getOrderStatus(orderRS.getByte("order_status_id")),
                        orderRS.getByte("payment_method_id"),
                        this.getPaymentMethod(orderRS.getByte("payment_method_id")),
                        orderRS.getString("contact_phone"),
                        orderRS.getString("delivery_address"),
                        this.getOrderItemsList(orderRS.getInt("cart_id")),
                        orderRS.getBigDecimal("order_total"),
                        orderRS.getTimestamp("order_time"),
                        orderRS.getString("order_note"),
                        orderRS.getTimestamp("delivery_time"),
                        orderRS.getTimestamp("order_cancel_time"));
                orderList.add(order);
            }
        } catch (SQLException ex) {
            Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return orderList;
    }

    public Order getOrder(int orderID) {
        Order order = null;
        try {
            ps = conn.prepareStatement("SELECT * FROM [Order] WHERE order_id = ?");
            ps.setInt(1, orderID);
            rs = ps.executeQuery();
            if (rs.next()) {
                order = new Order(
                        rs.getInt("order_id"),
                        rs.getInt("cart_id"),
                        rs.getInt("customer_id"),
                        rs.getByte("order_status_id"),
                        this.getOrderStatus(rs.getByte("order_status_id")),
                        rs.getByte("payment_method_id"),
                        this.getPaymentMethod(rs.getByte("payment_method_id")),
                        rs.getString("contact_phone"),
                        rs.getString("delivery_address"),
                        this.getOrderItemsList(rs.getInt("cart_id")),
                        rs.getBigDecimal("order_total"),
                        rs.getTimestamp("order_time"),
                        rs.getString("order_note"),
                        rs.getTimestamp("delivery_time"),
                        rs.getTimestamp("order_cancel_time"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return order;
    }

    public ResultSet getOrdersFromCustomer(int customerID) {
        String sql = "select * from [Order] where customer_id = ?";
        try {
            ps = conn.prepareStatement(sql);
            ps.setInt(1, customerID);
            rs = ps.executeQuery();
            return rs;
        } catch (SQLException ex) {
            Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public List<Order> getOrdersFromCustomerList(int customerID) {
        System.out.println("customerID: " + customerID);
        ResultSet orderRS = this.getOrdersFromCustomer(customerID);
        List<Order> orderList = null;
        try {
            orderList = new ArrayList<>();
            while (orderRS.next()) {
                Order order = new Order(orderRS.getInt("order_id"),
                        orderRS.getInt("cart_id"),
                        orderRS.getInt("customer_id"),
                        orderRS.getByte("order_status_id"),
                        this.getOrderStatus(orderRS.getByte("order_status_id")),
                        orderRS.getByte("payment_method_id"),
                        this.getPaymentMethod(orderRS.getByte("payment_method_id")),
                        orderRS.getString("contact_phone"),
                        orderRS.getString("delivery_address"),
                        this.getOrderItemsList(orderRS.getInt("cart_id")),
                        orderRS.getBigDecimal("order_total"),
                        orderRS.getTimestamp("order_time"),
                        orderRS.getString("order_note"),
                        orderRS.getTimestamp("delivery_time"),
                        orderRS.getTimestamp("order_cancel_time"));
                orderList.add(order);
            }
        } catch (SQLException ex) {
            Logger.getLogger(FoodDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return orderList;
    }

    public ResultSet getAll() {
        String sql = "SELECT * FROM [Order]";
        try {
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            return rs;
        } catch (SQLException ex) {
            Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public int add(Order order) {
        String sql = "INSERT INTO [Order] (cart_id, customer_id, order_status_id, payment_method_id, contact_phone, delivery_address, order_total, order_time, order_note, delivery_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        int result = 0;
        try {
            ps = conn.prepareStatement(sql);
            ps.setInt(1, order.getCartID());
            ps.setInt(2, order.getCustomerID());
            ps.setByte(3, order.getOrderStatusID());
            ps.setByte(4, order.getPaymentMethodID());
            ps.setString(5, order.getContactPhone());
            ps.setString(6, order.getDeliveryAddress());
            ps.setBigDecimal(7, order.getOrderTotal());
            ps.setTimestamp(8, order.getOrderTime());
            ps.setString(9, order.getOrderNote());
            ps.setTimestamp(10, order.getDeliveryTime());

            result = ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    public int update(Order order) {
        String sql = "UPDATE [Order] SET cart_id = ?, customer_id = ?, order_status_id = ?, payment_method_id = ?, contact_phone = ?, delivery_address = ?, order_total = ?, order_time = ?, order_note = ?, delivery_time = ?, order_cancel_time = ? WHERE order_id = ?";
        int result = 0;
        try {
            ps = conn.prepareStatement(sql);
            ps.setInt(1, order.getCartID());
            ps.setInt(2, order.getCustomerID());
            ps.setByte(3, order.getOrderStatusID());
            ps.setByte(4, order.getPaymentMethodID());
            ps.setString(5, order.getContactPhone());
            ps.setString(6, order.getDeliveryAddress());
            ps.setBigDecimal(7, order.getOrderTotal());
            ps.setTimestamp(8, order.getOrderTime());
            ps.setString(9, order.getOrderNote());
            ps.setTimestamp(10, order.getDeliveryTime());
            ps.setTimestamp(11, order.getOrderCancelTime());
            ps.setInt(12, order.getOrderID());
            result = ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    public int updateForAdmin(Order order) {
        String sql = "UPDATE [Order] SET order_status_id = ?, payment_method_id = ?, contact_phone = ?, delivery_address = ?, order_total = ?, order_note = ? WHERE order_id = ?";
        int result = 0;
        try {
            ps = conn.prepareStatement(sql);
            ps.setByte(1, order.getOrderStatusID());
            ps.setByte(2, order.getPaymentMethodID());
            ps.setString(3, order.getContactPhone());
            ps.setString(4, order.getDeliveryAddress());
            ps.setBigDecimal(5, order.getOrderTotal());
            ps.setString(6, order.getOrderNote());
            ps.setInt(7, order.getOrderID());
            result = ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    public int cancelOrder(int orderID, Timestamp cancelTime) {
        String sql = "UPDATE [Order] SET order_status_id = ?, order_cancel_time = ? WHERE order_id = ?";
        int result = 0;
        byte order_status = 5;
        try {
            ps = conn.prepareStatement(sql);
            ps.setByte(1, order_status);
            ps.setTimestamp(2, cancelTime);
            ps.setInt(3, orderID);
            result = ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    public String getPaymentMethod(byte paymentMethodID) {
        String paymentMethod = null;
        try {
            ps = conn.prepareStatement("SELECT * FROM PaymentMethod WHERE payment_method_id = ?");
            ps.setByte(1, paymentMethodID);
            rs = ps.executeQuery();
            if (rs.next()) {
                paymentMethod = rs.getString("payment_method");
            }
        } catch (SQLException ex) {
            Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return paymentMethod;
    }

    public String getOrderStatus(byte orderStatusID) {
        String orderStatus = null;
        try {
            ps = conn.prepareStatement("SELECT * FROM OrderStatus WHERE order_status_id = ?");
            ps.setByte(1, orderStatusID);
            rs = ps.executeQuery();
            if (rs.next()) {
                orderStatus = rs.getString("order_status");
            }
        } catch (SQLException ex) {
            Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return orderStatus;

    }

    public ResultSet getOrderItems(Order order) {
        int cartID = order.getCartID();
        String sql = "select food_name, food_quantity from Food join CartItem on Food.food_id = CartItem.food_id join Cart on CartItem.cart_id = Cart.cart_id and Cart.cart_id = ?";
        try {
            ps = conn.prepareStatement(sql);
            ps.setInt(1, cartID);
            rs = ps.executeQuery();
            return rs;
        } catch (SQLException ex) {
            Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public List<String> getOrderItemsList(Order order) {
        ResultSet orderItemsRS = this.getOrderItems(order);
        List<String> orderItems = null;
        try {
            orderItems = new ArrayList<>();
            while (orderItemsRS.next()) {
                String foodName = orderItemsRS.getString("food_name");
                short foodQuantity = orderItemsRS.getShort("food_quantity");
                orderItems.add(foodName + " x " + foodQuantity);
            }
        } catch (SQLException ex) {
            Logger.getLogger(FoodDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return orderItems;
    }

    public ResultSet getOrderItems(int cartID) {
        String sql = "select food_name, food_quantity from Food join CartItem on Food.food_id = CartItem.food_id join Cart on CartItem.cart_id = Cart.cart_id and Cart.cart_id = ?";
        try {
            ps = conn.prepareStatement(sql);
            ps.setInt(1, cartID);
            rs = ps.executeQuery();
            return rs;
        } catch (SQLException ex) {
            Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public List<String> getOrderItemsList(int cartID) {
        ResultSet orderItemsRS = this.getOrderItems(cartID);
        List<String> orderItems = null;
        try {
            orderItems = new ArrayList<>();
            while (orderItemsRS.next()) {
                String foodName = orderItemsRS.getString("food_name");
                short foodQuantity = orderItemsRS.getShort("food_quantity");
                orderItems.add(foodName + " x " + foodQuantity);
            }
        } catch (SQLException ex) {
            Logger.getLogger(FoodDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return orderItems;
    }

    public int delete(int id) {
        int result = 0;
        String deleteOrderLogSQL = "DELETE FROM OrderLog WHERE order_id = ?";
        String deleteOrderSQL = "DELETE FROM [Order] WHERE order_id = ?";

        PreparedStatement psOrderLog = null;
        PreparedStatement psOrder = null;

        try {
            // Start transaction
            conn.setAutoCommit(false);

            // Delete from OrderLog first
            psOrderLog = conn.prepareStatement(deleteOrderLogSQL);
            psOrderLog.setInt(1, id);
            psOrderLog.executeUpdate();

            // Delete from Orders table
            psOrder = conn.prepareStatement(deleteOrderSQL);
            psOrder.setInt(1, id);
            result = psOrder.executeUpdate();

            conn.commit();
        } catch (SQLException ex) {
            Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, "Error deleting order " + id, ex);
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException rollbackEx) {
                Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, "Error during rollback", rollbackEx);
            }
            throw new RuntimeException("Failed to delete order: " + id, ex);
        } finally {
            try {
                if (psOrderLog != null) {
                    psOrderLog.close();
                }
                if (psOrder != null) {
                    psOrder.close();
                }
                if (conn != null) {
                    conn.setAutoCommit(true);
                }
            } catch (SQLException finalEx) {
                Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, "Error in cleanup", finalEx);
            }
        }
        return result;
    }

    public int deleteMultiple(List<Integer> orderIDs) {
        if (orderIDs == null || orderIDs.isEmpty()) {
            return 0;
        }  

        int result = 0;
        try {
            conn.setAutoCommit(false);

            // Use batch processing for better performance
            String deleteOrderLogSQL = "DELETE FROM OrderLog WHERE order_id = ?";
            String deleteOrderSQL = "DELETE FROM [Order] WHERE order_id = ?";

            try (PreparedStatement psOrderLog = conn.prepareStatement(deleteOrderLogSQL); PreparedStatement psOrder = conn.prepareStatement(deleteOrderSQL)) {

                for (Integer orderID : orderIDs) {
                    // Delete from OrderLog
                    psOrderLog.setInt(1, orderID);
                    psOrderLog.addBatch();

                    // Delete from Order
                    psOrder.setInt(1, orderID);
                    psOrder.addBatch();
                }

                // Execute batches
                psOrderLog.executeBatch();
                int[] deleteResults = psOrder.executeBatch();

                // Count successful deletions
                for (int deleteResult : deleteResults) {
                    if (deleteResult > 0) {
                        result++;
                    }
                }

                conn.commit();
            }
        } catch (SQLException ex) {
            Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, "Error in batch deletion", ex);
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException rollbackEx) {
                Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, "Error during rollback", rollbackEx);
            }
            throw new RuntimeException("Failed to delete multiple orders", ex);
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                }
            } catch (SQLException finalEx) {
                Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, "Error resetting auto-commit", finalEx);
            }
        }
        return result;
    }

    public int updateOrderStatus(int order_id) {
        String sql = "UPDATE [Order] SET order_status_id = order_status_id + 1 WHERE order_id = ?";
        int result = 0;
        try {
            ps = conn.prepareStatement(sql);
            ps.setInt(1, order_id);
            result = ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    public int changeStatusMultiple(List<Integer> orderIDs) {
        int result = 0;
        try {
            conn.setAutoCommit(false); // Start transaction
            for (Integer orderID : orderIDs) {
                if (updateOrderStatus(orderID) == 1) {
                    result++; // Count number of successful status changes
                } else {
                    conn.rollback(); // Rollback transaction if deletion fails
                    return 0;
                }
            }
            conn.commit(); // Commit transaction if all deletions succeed
        } catch (SQLException ex) {
            Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, null, ex);
            try {
                conn.rollback(); // Rollback transaction if any exception occurs
            } catch (SQLException rollbackEx) {
                Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, null, rollbackEx);
            }
            return 0;
        } finally {
            try {
                conn.setAutoCommit(true); // Reset auto commit
            } catch (SQLException finalEx) {
                Logger.getLogger(OrderDAO.class.getName()).log(Level.SEVERE, null, finalEx);
            }
        }
        return result;
    }

}
