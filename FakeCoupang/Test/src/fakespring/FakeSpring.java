package fakespring;

import fakespring.hook.HookType;
import fakespring.hook.FakeSpringHook;

/**
 * Created by Coupang on 2016. 1. 1..
 */
public interface FakeSpring {
	void assemble(String packageName);
	void shutdown();
	<T> T getBean(Class<T> clazz);
	void registerHook(FakeSpringHook hook, HookType type);
}
