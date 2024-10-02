module dev.ikm.commons.service.discovery {
    uses dev.ikm.commons.service.loader.IKMServiceLoader;
    requires org.slf4j;
    requires org.eclipse.collections.api;
    requires dev.ikm.commons.service.loader;

    exports dev.ikm.commons.service.discovery;
    exports dev.ikm.commons.service.discovery.internal;
}