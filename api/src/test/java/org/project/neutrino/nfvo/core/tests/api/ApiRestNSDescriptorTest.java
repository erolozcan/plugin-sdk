package org.project.neutrino.nfvo.core.tests.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.project.neutrino.nfvo.api.RestNetworkService;
import org.project.neutrino.nfvo.api.exceptions.VNFDNotFoundException;
import org.project.neutrino.nfvo.catalogue.mano.descriptor.NetworkServiceDescriptor;
import org.project.neutrino.nfvo.catalogue.mano.descriptor.VirtualNetworkFunctionDescriptor;
import org.project.neutrino.nfvo.core.interfaces.NetworkServiceDescriptorManagement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApiRestNSDescriptorTest {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	@InjectMocks
	RestNetworkService restNetworkService;

	@Mock
	NetworkServiceDescriptorManagement nsdManagement;

	@Rule
	public ExpectedException exception = ExpectedException.none();

	private NetworkServiceDescriptor networkServiceDescriptor;

	private VirtualNetworkFunctionDescriptor virtualNetworkFunctionDescriptor;

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		networkServiceDescriptor = new NetworkServiceDescriptor();
		networkServiceDescriptor.setVendor("Fokus");
		virtualNetworkFunctionDescriptor = new VirtualNetworkFunctionDescriptor();
		virtualNetworkFunctionDescriptor.setVendor("Fokus");
		networkServiceDescriptor.getVnfd()
				.add(virtualNetworkFunctionDescriptor);
	}

	@Test
	public void testNSDFindAll() {
		List<NetworkServiceDescriptor> list = nsdManagement.query();
		when(nsdManagement.query()).thenReturn(list);
		assertEquals(list, restNetworkService.findAll());
	}

	@Test
	public void testNSDCreate() {
		when(nsdManagement.onboard(networkServiceDescriptor)).thenReturn(
				networkServiceDescriptor);
		NetworkServiceDescriptor networkServiceDescriptor2 = null;
		networkServiceDescriptor2 = restNetworkService
				.create(networkServiceDescriptor);
		assertEquals(networkServiceDescriptor, networkServiceDescriptor2);
	}

	@Test
	public void testNSDFindBy() {
		when(nsdManagement.query(networkServiceDescriptor.getId())).thenReturn(
				networkServiceDescriptor);
		assertEquals(networkServiceDescriptor,
				restNetworkService.findById(networkServiceDescriptor.getId()));
	}

	@Test
	public void testNSDUpdate() {
		when(
				nsdManagement.update(networkServiceDescriptor,
						networkServiceDescriptor.getId())).thenReturn(
				networkServiceDescriptor);
		assertEquals(networkServiceDescriptor, restNetworkService.update(
				networkServiceDescriptor, networkServiceDescriptor.getId()));
	}

	@Test
	public void testNSDDelete() {
		nsdManagement.delete(anyString());
		restNetworkService.delete(anyString());
	}

	// TODO from here
	@Test
	public void testpostVNFD() {
		List<VirtualNetworkFunctionDescriptor> list = new ArrayList<VirtualNetworkFunctionDescriptor>();
		networkServiceDescriptor.setVnfd(list);
		VirtualNetworkFunctionDescriptor vnfd = new VirtualNetworkFunctionDescriptor();
		vnfd.setName("test_VNFD");
		networkServiceDescriptor.getVnfd().add(vnfd);
		when(
				nsdManagement.update(networkServiceDescriptor,
						networkServiceDescriptor.getId())).thenReturn(
				networkServiceDescriptor);
		when(nsdManagement.query(networkServiceDescriptor.getId())).thenReturn(
				networkServiceDescriptor);
		NetworkServiceDescriptor nsdUpdate = nsdManagement.update(
				networkServiceDescriptor, networkServiceDescriptor.getId());
		VirtualNetworkFunctionDescriptor vnsDescriptor1 = restNetworkService
				.postVNFD(vnfd, networkServiceDescriptor.getId());

		List<VirtualNetworkFunctionDescriptor> listVnfds = nsdUpdate.getVnfd();
		for (VirtualNetworkFunctionDescriptor vnsDescriptor : listVnfds) {
			if (vnsDescriptor.getId().equals(vnfd.getId()))
				assertEquals(vnsDescriptor1, vnsDescriptor);
			else {
				fail("testpostVNFD FAILED: not found the VNFD into NSD");
			}
		}

	}

	@Test
	public void testgetVNFD() {
		when(nsdManagement.query(networkServiceDescriptor.getId())).thenReturn(
				networkServiceDescriptor);
		VirtualNetworkFunctionDescriptor vnfd = networkServiceDescriptor
				.getVnfd().get(0);
		assertEquals(vnfd,
				restNetworkService.getVirtualNetworkFunctionDescriptor(
						networkServiceDescriptor.getId(),
						networkServiceDescriptor.getVnfd().get(0).getId()));
	}

	@Test
	public void testgetVNFDs() {
		when(nsdManagement.query(networkServiceDescriptor.getId())).thenReturn(
				networkServiceDescriptor);
		List<VirtualNetworkFunctionDescriptor> vnfds = networkServiceDescriptor
				.getVnfd();
		assertEquals(
				vnfds,
				restNetworkService
						.getVirtualNetworkFunctionDescriptors(networkServiceDescriptor
								.getId()));

	}

	@Test
	public void testVNFDNotFoundException() {
		exception.expect(VNFDNotFoundException.class);
		when(nsdManagement.query(networkServiceDescriptor.getId())).thenReturn(
				networkServiceDescriptor);
		restNetworkService.getVirtualNetworkFunctionDescriptor(
				networkServiceDescriptor.getId(), "-1");
	}

	@Test
	public void testupdateVNF() {
		when(nsdManagement.query(networkServiceDescriptor.getId())).thenReturn(
				networkServiceDescriptor);
		VirtualNetworkFunctionDescriptor vnfd = new VirtualNetworkFunctionDescriptor();
		vnfd.setVendor("FOKUS");
		VirtualNetworkFunctionDescriptor vnfd_toUp = networkServiceDescriptor
				.getVnfd().get(0);
		log.info("" + vnfd_toUp);
		vnfd_toUp = vnfd;
		networkServiceDescriptor.getVnfd().add(vnfd_toUp);
		log.info("" + vnfd);
		assertEquals(
				vnfd,
				restNetworkService.updateVNF(vnfd,
						networkServiceDescriptor.getId(), vnfd_toUp.getId()));

	}

	@Test
	public void testdeleteVirtualNetworkFunctionDescriptor() {
		when(nsdManagement.query(networkServiceDescriptor.getId())).thenReturn(
				networkServiceDescriptor);
		VirtualNetworkFunctionDescriptor vnfd = networkServiceDescriptor.getVnfd().get(0);
		restNetworkService.deleteVirtualNetworkFunctionDescriptor(
				networkServiceDescriptor.getId(), vnfd.getId());
		log.info(""+networkServiceDescriptor);
	}
	// TODO to here
}
