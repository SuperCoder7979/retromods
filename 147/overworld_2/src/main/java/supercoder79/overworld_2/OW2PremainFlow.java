package supercoder79.overworld_2;

import nilloader.api.ClassTransformer;
import nilloader.api.NilLogger;

// Entrypoint for not-forge
public class OW2PremainFlow implements Runnable {

	@Override
	public void run() {
		OW2Logger.LOGGER.info("If Overworld 2 is so good, why isn't there an Overworld -2?");

		ClassTransformer.register(new WorldTypeTransformer());
		ClassTransformer.register(new WorldProviderTransformer());
	}

}
