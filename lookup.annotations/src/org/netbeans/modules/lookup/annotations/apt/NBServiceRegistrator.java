/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 2008 Sun Microsystems, Inc. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 * 
 * Contributor(s):
 * 
 * Portions Copyrighted 2008 Sun Microsystems, Inc.
 */
package org.netbeans.modules.lookup.annotations.apt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementScanner6;
import javax.lang.model.util.SimpleAnnotationValueVisitor6;
import javax.tools.Diagnostic.Kind;
import javax.tools.FileObject;
import javax.tools.JavaFileManager.Location;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.netbeans.modules.lookup.annotations.annotations.Contract;
import org.netbeans.modules.lookup.annotations.annotations.ContractProvided;
import org.netbeans.modules.lookup.annotations.annotations.ContractsProvided;
import org.netbeans.modules.lookup.annotations.annotations.Instantiator;
import org.netbeans.modules.lookup.annotations.annotations.Service;
import org.netbeans.modules.lookup.annotations.model.ContractM;
import org.netbeans.modules.lookup.annotations.model.InstantiatorM;
import org.netbeans.modules.lookup.annotations.model.ServiceM;
import org.netbeans.modules.lookup.annotations.model.Services;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * Simple META-INF/services registrator
 * Uses <i>net.java.visualvm.services.annotations.ServiceM</i> annotation to identify the service
 * @author Jaroslav Bachorik
 */
@SupportedAnnotationTypes(value = {"*"})
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class NBServiceRegistrator extends AbstractProcessor {

    /**
     * Annotation visitor for traversing all {@linkplain ContractProvided} annotations
     * contained in a {@linkplain ContractsProvided} annotation
     */
    private class ContractsProvidedAnnotationVisitor extends SimpleAnnotationValueVisitor6<Void, Set<ContractM>> {

        @Override
        public Void visitAnnotation(AnnotationMirror a, Set<ContractM> contracts) {
            processContractProvided(a, contracts);
            return null;
        }
    }
    private Collection<ServiceM> globalServices = new ArrayList<ServiceM>();
    private Document layerDocument;

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            try {
                processingEnv.getMessager().printMessage(Kind.NOTE, "Processing annotated services");
                writeRegistrations(globalServices);
            } catch (Exception e) {
                processingEnv.getMessager().printMessage(Kind.ERROR, "Processing failed!");
                processingEnv.getMessager().printMessage(Kind.ERROR, e.getLocalizedMessage());
                return false;
            }
            return true;
        }

        Services services = new Services();
        Services persistedServices = new Services();

        loadMetaInfServices(persistedServices, StandardLocation.SOURCE_PATH);

        loadMetaInfServices(services, StandardLocation.CLASS_OUTPUT);
        processAnnotatedServices(roundEnv, services);

        invalidateMetaInfServices(services);
        services.mergeFrom(persistedServices);

        globalServices.addAll(services.values());

//        debugRegistrations(services);

        return true;
    }

    /**
     * Goes through all classes annotated by {@linkplain Service} annotation
     * and retrieves the contracts implemented
     * 
     * @param roundEnv {@link RoundEnvironment} instance
     * @param services A bag to put the created services
     */
    private void processAnnotatedServices(RoundEnvironment roundEnv, Services services) {

        for (Element element : roundEnv.getElementsAnnotatedWith(Service.class)) {
            if (element.getKind() == ElementKind.CLASS) {
                TypeElement serviceElement = (TypeElement) element;
                String serviceName = processingEnv.getElementUtils().getBinaryName(serviceElement).toString();

                ServiceM service = services.get(serviceName);
                if (service == null) {
                    service = new ServiceM(serviceName);
                    services.put(service);
                }
                service.getContracts().addAll(findDefinedContracts(serviceElement));
                service.getContracts().addAll(findInheritedContracts(serviceElement));
                service.setInstantiator(findInstantiator(serviceElement));
            }
        }
    }

    /**
     * This method checks the services from the bag against the actual state
     * All services not comforming to the definition of a service are pruned from the bag
     * @param services The bag of services
     */
    private void invalidateMetaInfServices(Services services) {
        services.forEach(new Services.Functor() {

            public void execute(ServiceM service) {
                TypeElement serviceElement = processingEnv.getElementUtils().getTypeElement(service.getServiceClass());
                Set<ContractM> actualContracts = new HashSet<ContractM>();
                actualContracts.addAll(findInheritedContracts(serviceElement));
                actualContracts.addAll(findDefinedContracts(serviceElement));

                service.getContracts().retainAll(actualContracts);
            }
        });
    }

    private File getLocation(Location serviceLocation) throws Exception {
        // ********** This is a hack to get hold of the source file location; there is no other way :( *******
        Field fManagerFld = processingEnv.getFiler().getClass().getDeclaredField("fileManager"); // NOI18N
        fManagerFld.setAccessible(true);

        StandardJavaFileManager jfm = (StandardJavaFileManager) fManagerFld.get(processingEnv.getFiler());
        // ********* End of hack *****************************************************************************

        for (File location : jfm.getLocation(serviceLocation)) {
            if (location != null) {
                return location;
            }
        }
        return null;
    }

    /**
     * Loads services defined in a <i>META-INF/services</i> location
     * @param services The services bag to load the new services to
     * @param serviceLocation A {@link Location} specifying where to load the services from
     */
    private void loadMetaInfServices(Services services, Location serviceLocation) {
        try {
            File location = getLocation(serviceLocation);
            if (location != null) {
                loadMetaInfServices(services, location.getAbsolutePath() + File.separator + "META-INF" + File.separator + "services"); // NOI18N
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads services defined in a <i>META-INF/services</i> folder under the given path
     * @param services The services bag to load the new services to
     * @param srcPath The path to look for <i>META-INF/services</i>
     * @throws java.io.IOException
     */
    private static void loadMetaInfServices(Services services, String srcPath) throws IOException {
        File servicesFolder = new File(srcPath);
        if (!servicesFolder.exists() || !servicesFolder.isDirectory()) {
            return;
        }
        for (File serviceFile : servicesFolder.listFiles()) {
            if (serviceFile.isFile()) {
                ContractM contract = new ContractM(serviceFile.getName());
                List<String> lines = loadLines(serviceFile);
                for (String line : lines) {
                    ServiceM service = services.get(line);
                    if (service == null) {
                        service = new ServiceM(line);
                        services.put(service);
                    }
                    service.getContracts().add(contract);
                }
            }
        }
    }

    private void loadLayer() throws Exception {
        File layerFile = getLayerFile(StandardLocation.SOURCE_PATH);
        if (layerFile != null) {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            layerDocument = builder.parse(layerFile);
            layerDocument.setXmlStandalone(true);

        }
    }

    private File getManifest() throws Exception {
        File location = getLocation(StandardLocation.SOURCE_PATH);
        if (location != null) {
            return new File(location.getParent() + File.separator + "manifest.mf"); // NOI18N
        }
        return null;
    }

    private File getLayerFile(Location location) throws Exception {
        String layerPath = getLayerRelativePath();
        if (layerPath != null) {
            return new File(getLocation(location) + File.separator + layerPath);
        }
        return null;
    }

    private String getLayerRelativePath() {
        String layerPath = null;
        try {
            File manifest = getManifest();
            if (manifest != null) {

                for (String line : loadLines(manifest)) {
                    if (line.startsWith("OpenIDE-Module-Layer:")) { // NOI18N
                        int startIndex = line.lastIndexOf(":");
                        if (startIndex > -1) {
                            layerPath = line.substring(startIndex + 1).trim();
                            break;
                        }
                    }
                }
            }
        } catch (Exception ex) {
        }
        return layerPath;
    }

    private static List<String> loadLines(File file) {
        List<String> lines = new ArrayList<String>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            try {
                do {
                    line = br.readLine();
                    if (line != null) {
                        lines.add(line);
                    }
                } while (line != null);
            } finally {
                br.close();
            }
        } catch (Exception e) {
            lines.clear();
        }
        return lines;
    }

    /**
     * Finds all {@linkplain Contract}s deined up the class hierarchy starting by the given class
     * @param serviceElement The class to start searching for inherited {@linkplain Contract}s
     * @return Returns a set of inherited contract
     */
    private Set<ContractM> findInheritedContracts(TypeElement serviceElement) {
        if (serviceElement == null) {
            return Collections.EMPTY_SET;
        }
        Set<ContractM> contracts = new HashSet<ContractM>();
        for (TypeMirror contractMirror : serviceElement.getInterfaces()) {
            TypeElement contractElement = (TypeElement) processingEnv.getTypeUtils().asElement(contractMirror);
            if (contractElement.getAnnotation(Contract.class) != null) {
                contracts.add(new ContractM(processingEnv.getElementUtils().getBinaryName(contractElement).toString()));
            }
        }
        contracts.addAll(findInheritedContracts((TypeElement) processingEnv.getTypeUtils().asElement(serviceElement.getSuperclass())));

        return contracts;

    }

    /**
     * This method will try to find a public static method annotated by {@linkplain Instantiator} annotation
     * @param serviceElement The service element to search for instantiator
     * @return Returns an instantiator if found or {@linkplain InstantiatorM.DEFAULT}
     */
    private InstantiatorM findInstantiator(TypeElement serviceElement) {
        if (serviceElement == null) {
            return InstantiatorM.DEFAULT;
        }
        InstantiatorM result = serviceElement.accept(new ElementScanner6<InstantiatorM, Void>() {

            InstantiatorM instantiator = InstantiatorM.DEFAULT;

            @Override
            public InstantiatorM visitExecutable(ExecutableElement e, Void p) {
                if (instantiator != InstantiatorM.DEFAULT) {
                    return instantiator;
                }

                if (e.getAnnotation(Instantiator.class) != null) {
                    if (e.getModifiers().contains(Modifier.STATIC) && e.getModifiers().contains(Modifier.PUBLIC)) {
                        instantiator = new InstantiatorM(e.getSimpleName().toString());
                    }
                }

                return instantiator;
            }
        }, null);
        return result != null ? result : InstantiatorM.DEFAULT;
    }

    /**
     * Retrieves a set of contracts defined by {@linkplain ContractProvided} annotations on a class
     * @param serviceElement The class to inspect
     * @return Returns a set of defined contracts
     */
    private Set<ContractM> findDefinedContracts(TypeElement serviceElement) {
        if (serviceElement == null) {
            return Collections.EMPTY_SET;
        }
        Set<ContractM> contracts = new HashSet<ContractM>();
        for (AnnotationMirror annMirror : serviceElement.getAnnotationMirrors()) {
            TypeElement annElement = (TypeElement) annMirror.getAnnotationType().asElement();
            String annTypeName = processingEnv.getElementUtils().getBinaryName(annElement).toString();

            if (annTypeName.equals(ContractsProvided.class.getName())) {
                for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : annMirror.getElementValues().entrySet()) {
                    if (entry.getKey().getSimpleName().toString().equals("value")) { // NOI18N

                        List<AnnotationValue> contractList = (List<AnnotationValue>) entry.getValue().getValue();
                        for (AnnotationValue contr : contractList) {
                            contr.accept(new ContractsProvidedAnnotationVisitor(), contracts);
                        }
                    }
                }
            }
            if (annTypeName.equals(ContractProvided.class.getName())) {
                processContractProvided(annMirror, contracts);
            }

        }

        return contracts;

    }

    /**
     * Debugging output
     * @param services Bag of services
     */
    private void debugRegistrations(Map<String, ServiceM> services) {
        for (Map.Entry<String, ServiceM> entry : services.entrySet()) {
            processingEnv.getMessager().printMessage(Kind.NOTE, "Service: " + entry.getKey()); // NOI18N
            processingEnv.getMessager().printMessage(Kind.NOTE, "=============================================================="); // NOI18N
            for (ContractM contract : entry.getValue().getContracts()) {
                processingEnv.getMessager().printMessage(Kind.NOTE, "* " + contract.getContractClass()); // NOI18N
            }
        }
    }

    /**
     * Writes the defined services to <i>META-INF/services</i> location
     * @param services The services to write
     * @throws Exception
     */
    private void writeRegistrations(Collection<ServiceM> services) throws Exception {
        writeLayer(services);
        writeMetaInf(services);
    }

    private void writeMetaInf(Collection<ServiceM> services) throws IOException {
        Map<String, Set<String>> registrations = preprocessMetaInf(services);

        String servicesPath = null;
        for (Map.Entry<String, Set<String>> entry : registrations.entrySet()) {
            final String METAINF = "META-INF/services"; // NOI18N
            String resourcePath = METAINF + "/" + entry.getKey();
            Writer writer = null;
            try {
                FileObject fo = processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", resourcePath, (Element) null); // NOI18N
                if (servicesPath == null) {
                    servicesPath = fo.toUri().getPath();
                    servicesPath = servicesPath.substring(0, servicesPath.lastIndexOf(METAINF) + METAINF.length());
                }
                writer = fo.openWriter();
                for (String serviceClass : entry.getValue()) {
                    writer.write(serviceClass + "\n"); // NOI18N
                }
            } finally {
                if (writer != null) {
                    writer.close();
                } else {
                    throw new IOException();
                }
            }
        }
        cleanupMetaInf(servicesPath, registrations);
    }

    private void writeLayer(Collection<ServiceM> services) throws Exception {
        org.w3c.dom.Element insertionPoint = prepareLayer();

        for (ServiceM service : services) {
            if (service.getInstantiator() != InstantiatorM.DEFAULT) {
                String instanceName = service.getServiceClass().replace('.', '-') + ".instance";
                org.w3c.dom.Element regElement = layerDocument.createElement("file");
                regElement.setAttribute("name", instanceName);
                org.w3c.dom.Element instAttr = layerDocument.createElement("attr");
                instAttr.setAttribute("name", "instanceCreate");
                instAttr.setAttribute("methodvalue", service.getServiceClass() + "." + service.getInstantiator().getMethodName());
                regElement.appendChild(instAttr);
                for(ContractM contract : service.getContracts()) {
                    org.w3c.dom.Element contractElem = layerDocument.createElement("attr");
                    contractElem.setAttribute("name", "instanceOf");
                    contractElem.setAttribute("stringvalue", contract.getContractClass());
                    regElement.appendChild(contractElem);
                }
                insertionPoint.appendChild(regElement);
            }
        }

        FileObject fo = processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", getLayerRelativePath(), (Element) null); // NOI18N

        DOMSource domSource = new DOMSource(layerDocument);
        StreamResult streamResult = new StreamResult(fo.openWriter());
        TransformerFactory.newInstance().newTransformer().transform(domSource, streamResult);
    }

    private org.w3c.dom.Element prepareLayer() throws Exception {
        org.w3c.dom.Element annotatedElement = null;
        loadLayer();
        org.w3c.dom.Element servicesElement = findFolder("Services");
        if (servicesElement == null) {
            servicesElement = layerDocument.createElement("folder");
            servicesElement.setAttribute("name", "Services");
            layerDocument.getDocumentElement().appendChild(servicesElement);
        }
        annotatedElement = findFolder(servicesElement, "Annotated");
        if (annotatedElement == null) {
            annotatedElement = layerDocument.createElement("folder");
            annotatedElement.setAttribute("name", "Annotated");
            servicesElement.appendChild(annotatedElement);
        }
        return annotatedElement;
    }

    /**
     * A helper method to convert the service registrations from the internal
     * representation to the one compatible with the <i>META-INF/services</i>
     * registration paradigm.
     * @param services The services to process
     * @return Returns an in-memory representation of the <i>META-INF/services</i> registrations
     */
    private Map<String, Set<String>> preprocessMetaInf(Collection<ServiceM> services) {
        Map<String, Set<String>> registrations = new HashMap<String, Set<String>>();
        for (ServiceM service : services) {
            if (service.getInstantiator() != InstantiatorM.DEFAULT) continue;
            for (ContractM contract : service.getContracts()) {
                Set<String> registeredServices = registrations.get(contract.getContractClass());
                if (registeredServices == null) {
                    registeredServices = new HashSet<String>();
                    registrations.put(contract.getContractClass(), registeredServices);
                }
                registeredServices.add(service.getServiceClass());
            }
        }
        return registrations;
    }

    /**
     * A helper method to process the contents of {@linkplain ContractProvided} annotation
     * @param annMirror The annotation mirror
     * @param contracts The set of contracts to add the new contract to
     */
    private void processContractProvided(AnnotationMirror annMirror, Set<ContractM> contracts) {
        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : annMirror.getElementValues().entrySet()) {
            if (entry.getKey().getSimpleName().toString().equals("value")) {
                contracts.add(new ContractM(entry.getValue().toString().replace(".class", "")));
            }
        }
    }

    /**
     * Checks the <i>META-INF/services</i> style registrations in the given path
     * It removes all file-based registrations without their corresponding in-memory
     * representation.
     * @param servicesPath The path to look for the registrations
     * @param registrations The in-memory registrations
     */
    private void cleanupMetaInf(String servicesPath, Map<String, Set<String>> registrations) {
        if (servicesPath != null) {
            File servicesDir = new File(servicesPath);
            if (servicesDir != null && servicesDir.isDirectory()) {
                for (File regFile : servicesDir.listFiles()) {
                    if (!registrations.containsKey(regFile.getName())) {
                        regFile.delete();
                    }
                }
            }
        }
    }

    private org.w3c.dom.Element findFolder(String folderName) {
        if (layerDocument != null) {
            return findFolder(layerDocument.getDocumentElement(), folderName);
        } else {
            return null;
        }
    }

    private org.w3c.dom.Element findFolder(org.w3c.dom.Element parent, String folderName) {
        NodeList folders = parent.getElementsByTagName("folder");
        if (folders != null) {
            for (int i = 0; i < folders.getLength(); i++) {
                if (folders.item(i).getAttributes().getNamedItem("name").getNodeValue().equals(folderName)) {
                    return (org.w3c.dom.Element) folders.item(i);
                }
            }
        }
        return null;
    }
}
