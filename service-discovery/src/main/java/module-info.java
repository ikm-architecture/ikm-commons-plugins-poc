import dev.ikm.commons.service.loader.PluginServiceLoader;

module dev.ikm.commons.service.discovery {
    uses PluginServiceLoader;
    requires org.slf4j;
    requires org.eclipse.collections.api;
    requires dev.ikm.commons.service.loader;

    exports dev.ikm.commons.service.discovery;
    exports dev.ikm.commons.service.discovery.internal;
}