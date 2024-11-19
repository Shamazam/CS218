package Server;

import API.DentalService;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class DentalServer {
    public static void main(String[] args) {
        try {
            // Create an instance of the implementation class
            DentalService service = new DentalServiceImpl();

            // Start the RMI registry on port 420
            LocateRegistry.createRegistry(420);

            // Bind the implementation to the registry with the name "DentalService"
            Naming.rebind("rmi://localhost:420/DentalService", service);

            System.out.println("DentalService is running...");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
