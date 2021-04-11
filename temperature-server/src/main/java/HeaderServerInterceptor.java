import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;

import java.util.Set;

public class HeaderServerInterceptor implements ServerInterceptor {
    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call,
                                                                 Metadata metadata,
                                                                 ServerCallHandler<ReqT, RespT> serverCallHandler) {

        if (call.getMethodDescriptor().getFullMethodName().equalsIgnoreCase("SensorService/GetById")) {
            final Set<String> keys = metadata.keys();
            for (String key : keys) {
                System.out.println(key + " : " + metadata.get(Metadata.Key.of(key, Metadata.ASCII_STRING_MARSHALLER)));
            }
        }

        return serverCallHandler.startCall(call, metadata);
    }
}
