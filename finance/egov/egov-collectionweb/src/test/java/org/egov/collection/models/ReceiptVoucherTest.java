package org.egov.erpcollection.models;
import org.egov.models.AbstractPersistenceServiceTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ReceiptVoucherTest extends AbstractPersistenceServiceTest<ReceiptVoucher,Long> {
	private CollectionObjectFactory factory;
	
	public ReceiptVoucherTest() {		
		this.type = ReceiptVoucher.class;
	}
	
	@Before
	public void setUp() {
		factory = new CollectionObjectFactory(session);
	}
	
	@Test
	public void testcreateReceiptMisc()
	{
		Assert.assertNotNull(service.create(factory.createReceiptVoucher()));
	}
}
