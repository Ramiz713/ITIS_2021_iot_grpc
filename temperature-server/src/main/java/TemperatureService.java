import com.itis.grpc.Messages;
import com.itis.grpc.TemperatureServiceGrpc;
import io.grpc.stub.StreamObserver;

public class TemperatureService extends TemperatureServiceGrpc.TemperatureServiceImplBase {

    @Override
    public void getRecordById(Messages.GetByIdRequest request, StreamObserver<Messages.TemperatureRecordResponse> responseObserver) {
        for (Messages.TemperatureRecord record : TemperatureRecords.getInstance()) {
            if (record.getId() == request.getId()) {
                final Messages.TemperatureRecordResponse temperatureResponse = Messages.TemperatureRecordResponse
                        .newBuilder()
                        .setTemperatureRecord(record)
                        .build();
                responseObserver.onNext(temperatureResponse);
                responseObserver.onCompleted();
                return;
            }
        }
        responseObserver.onError(new Exception("TemperatureSensor not found with id " + request.getId()));
    }

    @Override
    public void getAllByDeviceId(Messages.GetAllByDeviceIdRequest request, StreamObserver<Messages.TemperatureRecordResponse> responseObserver) {
        for (Messages.TemperatureRecord record : TemperatureRecords.getInstance()) {
            final Messages.TemperatureRecordResponse response = Messages.TemperatureRecordResponse.newBuilder()
                    .setTemperatureRecord(record)
                    .build();
            responseObserver.onNext(response);
        }
        responseObserver.onCompleted();
    }

    @Override
    public void saveRecord(Messages.TemperatureRecordRequest request, StreamObserver<Messages.TemperatureRecordResponse> responseObserver) {
        TemperatureRecords.getInstance().add(request.getTemperatureRecord());
        responseObserver.onNext(Messages.TemperatureRecordResponse.newBuilder()
                .setTemperatureRecord(request.getTemperatureRecord())
                .build());
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<Messages.TemperatureRecordRequest> saveAll(final StreamObserver<Messages.TemperatureRecordResponse> responseObserver) {
        return getTemperatureRequestStreamObserver(responseObserver);
    }

    private StreamObserver<Messages.TemperatureRecordRequest> getTemperatureRequestStreamObserver(final StreamObserver<Messages.TemperatureRecordResponse> responseObserver) {
        return new StreamObserver<Messages.TemperatureRecordRequest>() {
            @Override
            public void onNext(Messages.TemperatureRecordRequest value) {
                TemperatureRecords.getInstance().add(value.getTemperatureRecord());
                responseObserver.onNext(
                        Messages.TemperatureRecordResponse
                                .newBuilder()
                                .setTemperatureRecord(value.getTemperatureRecord())
                                .build()
                );
            }

            @Override
            public void onError(Throwable t) {
                System.out.println(t);
            }

            @Override
            public void onCompleted() {
                System.out.println("onCompleted");
                for (Messages.TemperatureRecord temperatureSensor : TemperatureRecords.getInstance()) {
                    System.out.println(temperatureSensor);
                }
                responseObserver.onCompleted();
            }
        };
    }

}
