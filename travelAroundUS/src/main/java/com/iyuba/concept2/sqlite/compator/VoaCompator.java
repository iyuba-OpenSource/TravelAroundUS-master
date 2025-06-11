package com.iyuba.concept2.sqlite.compator;

import com.iyuba.concept2.sqlite.mode.Voa;

import java.util.Comparator;

public class VoaCompator implements Comparator<Voa>{

	public int compare(Voa voa1, Voa voa2) {
		if(voa1.titleFind < voa2.titleFind) {
			return 1;
		} else if(voa1.titleFind == voa2.titleFind) {
			if(voa1.titleFind < voa2.titleFind) {
				return 1;
			} else if(voa1.titleFind == voa2.titleFind) {
				return 0;
			} else {
				return -1;
			}
		} else {
			return -1;
		}
	}
}
