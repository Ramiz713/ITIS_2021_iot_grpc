import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerInterceptors;
import io.grpc.ServerServiceDefinition;

import java.io.IOException;

public class TemperatureServer {

    private Server server;

    public static void main(String[] args) throws IOException, InterruptedException {
        TemperatureServer employeeServer = new TemperatureServer();
        employeeServer.start();
    }

    private void start() throws IOException, InterruptedException {
        final int port = 9000;

        TemperatureService temperatureService = new TemperatureService();

        final ServerServiceDefinition serverServiceDefinition = ServerInterceptors.interceptForward(temperatureService, new HeaderServerInterceptor());

        server = ServerBuilder.forPort(port)
                .addService(serverServiceDefinition)
                .build()
                .start();

        System.out.println("Listening on port " + port);

        server.awaitTermination();
    }
}
