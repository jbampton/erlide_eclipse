package org.erlide.testing.java;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.erlide.runtime.backend.InstallationInfo;
import org.junit.Test;

public class InfoElementTest {

	@Test
	public void codePath_installation_1() {
		InstallationInfo info = new InstallationInfo();
		List<String> pa = info.getCodePath();
		assertTrue(pa.size() == 1);
		assertTrue(pa.get(0).equals(InstallationInfo.DEFAULT_MARKER));
	}

	@Test
	public void codePathA_installation_1() {
		InstallationInfo info = new InstallationInfo();
		List<String> p = info.getCodePath();
		p.add("zzz");
		p.add(0, "aaa");
		assertTrue(p.size() == 3);
		List<String> pa = info.getPathA();
		assertTrue(pa.size() == 1);
		assertTrue(pa.get(0).equals("aaa"));
	}

	@Test
	public void codePathZ_installation_1() {
		InstallationInfo info = new InstallationInfo();
		List<String> p = info.getCodePath();
		p.add("zzz");
		p.add(0, "aaa");
		assertTrue(p.size() == 3);
		List<String> pz = info.getPathZ();
		assertTrue(pz.size() == 1);
		assertTrue(pz.get(0).equals("zzz"));
	}
}
