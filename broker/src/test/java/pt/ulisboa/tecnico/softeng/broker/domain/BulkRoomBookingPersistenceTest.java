package pt.ulisboa.tecnico.softeng.broker.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;
import pt.ist.fenixframework.FenixFramework;

public class BulkRoomBookingPersistenceTest {
	private static final String BROKER_NAME = "Happy Going";
	private static final String BROKER_CODE = "BK1017";

	private final LocalDate arrival = new LocalDate(2016, 12, 19);
	private final LocalDate departure = new LocalDate(2016, 12, 21);
	private final int number = 1;

	@Test
	public void success() {
		atomicProcess();
		atomicAssert();
	}

	@Atomic(mode = TxMode.WRITE)
	public void atomicProcess() {
		Broker broker = new Broker(BROKER_CODE, BROKER_NAME);
		broker.bulkBooking(number, arrival, departure);
	}

	@Atomic(mode = TxMode.READ)
	public void atomicAssert() {
		assertEquals(1, FenixFramework.getDomainRoot().getBrokerSet().size());

		List<Broker> brokers = new ArrayList<>(FenixFramework.getDomainRoot().getBrokerSet());
		Broker broker = brokers.get(0);

		assertEquals(BROKER_CODE, broker.getCode());
		assertEquals(BROKER_NAME, broker.getName());

		List<BulkRoomBooking> bulkRoomBookings = new ArrayList<>(broker.getBulkRoomBookingSet());
		assertEquals(1, bulkRoomBookings.size());
		
		BulkRoomBooking bb = bulkRoomBookings.get(0);
		assertEquals(number,bb.getNumber());
		assertEquals(arrival,bb.getArrival());
		assertEquals(departure,bb.getDeparture());
		
		// nao foram adicionados quartos no hotel
		// nao vai devolver nenhum quarto para reservar
		// logo vai haver uma excepcao do hotel
		assertEquals(1,bb.getNumberOfHotelExceptions());
		assertEquals(0,bb.getNumberOfRemoteErrors());
		assertFalse(bb.getCancelled());
	}

	@After
	@Atomic(mode = TxMode.WRITE)
	public void tearDown() {
		for (Broker broker : FenixFramework.getDomainRoot().getBrokerSet()) {
			broker.delete();
		}
	}
}

