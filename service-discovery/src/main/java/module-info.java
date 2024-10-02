import dev.ikm.commons.service.loader.ServiceLoader;

module dev.ikm.commons.service.discovery {
    uses ServiceLoader;
    requires org.slf4j;
    requires org.eclipse.collections.api;
    requires dev.ikm.commons.service.loader;

    exports dev.ikm.commons.service.discovery;
    exports dev.ikm.commons.service.discovery.internal;
}