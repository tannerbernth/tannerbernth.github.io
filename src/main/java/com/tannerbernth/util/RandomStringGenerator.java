package com.tannerbernth.util;

import java.util.Random;
import com.tannerbernth.util.RandomType;

import org.springframework.beans.factory.annotation.Autowired;

public class RandomStringGenerator {

	@Autowired
	Random random;
	// ASCII
	// 48-57  (0-9)
	// 65-90  (A-Z)
	// 97-122 (a-z)
	
	public RandomStringGenerator() {
		super();
	}

	public String generate(int length, RandomType type) {
		int min, max;
		switch (type) {
			case NUMBER:
				min = 48; 	// 0
				max = 57; 	// 9
				break;
			case UPPERCASE:
				min = 65; 	// A
				max = 90; 	// Z
				break;
			case LOWERCASE:
				min = 97; 	// a
				max = 122; 	// z
				break;
			default: 
				min = 97; 	// a
				max = 122; 	// z
				break;
		}
		StringBuilder buffer = new StringBuilder();

		for (int i = 0; i < length; i++) {
			int randomChar = min + (int)(random.nextFloat() * (max - min + 1));
			buffer.append((char) randomChar);
		}

		return buffer.toString();
	}

	public String generate(int length) {
		int min = 97,
			max = 122;
		StringBuilder buffer = new StringBuilder();

		for (int i = 0; i < length; i++) {
			int randomChar = min + (int)(random.nextFloat() * (max - min + 1));
			buffer.append((char) randomChar);
		}

		return buffer.toString();
	}
}