package com.takipi.oss.benchmarks.jmh.loops;

import org.openjdk.jmh.annotations.*;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@State(Scope.Benchmark)
public class LoopBenchmarkPrimitiveInt {
	volatile int size = 100000;
	volatile int[] integers = null;

	public static void main(String[] args) {
		LoopBenchmarkPrimitiveInt benchmark = new LoopBenchmarkPrimitiveInt();
		benchmark.setup();

		System.out.println("forEachLoopMaxInteger max is: " + benchmark.forEachLoopMaxInteger());
		System.out.println("forEachLambdaMaxInteger max is: " + benchmark.forEachLambdaMaxInteger());
		System.out.println("forMaxInteger max is: " + benchmark.forMaxInteger());
		System.out.println("parallelStreamMaxInteger max is: " + benchmark.parallelStreamMaxInteger());
		System.out.println("streamMaxInteger max is: " + benchmark.streamMaxInteger());
		System.out.println("iteratorMaxInteger max is: " + benchmark.lambdaMaxInteger());
	}

	@Setup
	public void setup() {
		integers = new int[size];
		populate(integers);
	}

	public void populate(int[] list) {
		Random random = new Random();
		for (int i = 0; i < size; i++) {
			list[i] = random.nextInt(1000000);
		}
	}

	@Benchmark
	@BenchmarkMode(Mode.AverageTime)
	@OutputTimeUnit(TimeUnit.MILLISECONDS)
	@Fork(2)
	@Measurement(iterations = 5)
	@Warmup(iterations = 5)
	public int forEachLoopMaxInteger() {
		int max = Integer.MIN_VALUE;
		for (int n : integers) {
			max = Integer.max(max, n);
		}
		return max;
	}

	@Benchmark
	@BenchmarkMode(Mode.AverageTime)
	@OutputTimeUnit(TimeUnit.MILLISECONDS)
	@Fork(2)
	@Measurement(iterations = 5)
	@Warmup(iterations = 5)
	public int forEachLambdaMaxInteger() {
		final Wrapper wrapper = new Wrapper();
		wrapper.inner = Integer.MIN_VALUE;

		IntStream.of(integers).forEach(i -> helper(i, wrapper));
		return wrapper.inner;
	}

	public static class Wrapper {
		public int inner;
	}

	private int helper(int i, Wrapper wrapper) {
		wrapper.inner = Math.max(i, wrapper.inner);
		return wrapper.inner;
	}

	@Benchmark
	@BenchmarkMode(Mode.AverageTime)
	@OutputTimeUnit(TimeUnit.MILLISECONDS)
	@Fork(2)
	@Measurement(iterations = 5)
	@Warmup(iterations = 5)
	public int forMaxInteger() {
		int max = Integer.MIN_VALUE;
		for (int i = 0; i < size; i++) {
			max = Integer.max(max, integers[i]);
		}
		return max;
	}

	@Benchmark
	@BenchmarkMode(Mode.AverageTime)
	@OutputTimeUnit(TimeUnit.MILLISECONDS)
	@Fork(2)
	@Measurement(iterations = 5)
	@Warmup(iterations = 5)
	public int parallelStreamMaxInteger() {
		OptionalInt max = IntStream.of(integers).parallel().reduce(Integer::max);
		return max.getAsInt();
	}

	@Benchmark
	@BenchmarkMode(Mode.AverageTime)
	@OutputTimeUnit(TimeUnit.MILLISECONDS)
	@Fork(2)
	@Measurement(iterations = 5)
	@Warmup(iterations = 5)
	public int streamMaxInteger() {
		OptionalInt max = IntStream.of(integers).reduce(Integer::max);
		return max.getAsInt();
	}

	@Benchmark
	@BenchmarkMode(Mode.AverageTime)
	@OutputTimeUnit(TimeUnit.MILLISECONDS)
	@Fork(2)
	@Measurement(iterations = 5)
	@Warmup(iterations = 5)
	public int lambdaMaxInteger() {
		return IntStream.of(integers).reduce(Integer.MIN_VALUE, (a, b) -> Integer.max(a, b));
	}
}
