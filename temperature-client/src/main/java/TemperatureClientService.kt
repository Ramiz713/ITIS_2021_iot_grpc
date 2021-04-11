import com.itis.grpc.Messages
import com.itis.grpc.TemperatureServiceGrpc
import io.grpc.ManagedChannelBuilder
import java.util.concurrent.TimeUnit
import io.grpc.stub.StreamObserver
import java.util.ArrayList

fun main(args: Array<String>) {
    val channel = ManagedChannelBuilder.forAddress("localhost", 9000)
        .usePlaintext()
        .build()
    //блокирующий клиент
    val blockingClient = TemperatureServiceGrpc.newBlockingStub(channel)

    //неблокир. клиет
    val nonBlockingClient = TemperatureServiceGrpc.newStub(channel)
    when (args.getOrNull(0)?.toInt()) {
        1 -> GetRecordById(blockingClient)
        2 -> GetAllByDeviceId(blockingClient)
        3 -> saveAll(nonBlockingClient)
        4 -> save(nonBlockingClient)
    }
    Thread.sleep(1000)
    channel.shutdown()
    channel.awaitTermination(1, TimeUnit.SECONDS)
}

private fun save(nonBlockingClient: TemperatureServiceGrpc.TemperatureServiceStub) {
    val record = Messages.TemperatureRecord.newBuilder()
        .setId(177)
        .setScale("C")
        .setTemperature(-23f)
        .build()

    val employeeRequest = Messages.TemperatureRecordRequest.newBuilder()
        .setTemperatureRecord(record)
        .build()
    val stream = object : StreamObserver<Messages.TemperatureRecordResponse?> {
        override fun onNext(value: Messages.TemperatureRecordResponse?) {
            println(value?.temperatureRecord)
        }
        override fun onError(t: Throwable) {}
        override fun onCompleted() = println("onCompleted")
    }
    nonBlockingClient.saveRecord(employeeRequest, stream)
}

private fun saveAll(nonBlockingClient: TemperatureServiceGrpc.TemperatureServiceStub) {
    val records = listOf<Messages.TemperatureRecord>(
        Messages.TemperatureRecord.newBuilder()
            .setId(123)
            .setTemperature(37f)
            .setScale("C")
            .build(),
        Messages.TemperatureRecord.newBuilder()
            .setId(124)
            .setTemperature(5f)
            .setScale("K")
            .build()
    )
    val stream = nonBlockingClient.saveAll(object : StreamObserver<Messages.TemperatureRecordResponse?> {
        override fun onNext(value: Messages.TemperatureRecordResponse?) {
            println(value?.temperatureRecord)
        }

        override fun onError(t: Throwable) {}
        override fun onCompleted() {
            println("onCompleted")
        }
    })
    records.forEach {
        val employeeRequest = Messages.TemperatureRecordRequest.newBuilder()
            .setTemperatureRecord(it)
            .build()
        stream.onNext(employeeRequest)
        Thread.sleep(1000)
    }
    stream.onCompleted()
}

private fun GetAllByDeviceId(blockingClient: TemperatureServiceGrpc.TemperatureServiceBlockingStub) {
    val temperatureRecordResponseIterator =
        blockingClient.getAllByDeviceId(Messages.GetAllByDeviceIdRequest.newBuilder().build())
    while (temperatureRecordResponseIterator.hasNext()) {
        println(temperatureRecordResponseIterator.next().temperatureRecord)
    }
}

private fun GetRecordById(blockingClient: TemperatureServiceGrpc.TemperatureServiceBlockingStub) {
    val temperatureRecordResponse = blockingClient.getRecordById(
        Messages.GetByIdRequest.newBuilder()
            .setId(76)
            .build()
    )
    println(temperatureRecordResponse.temperatureRecord)
}