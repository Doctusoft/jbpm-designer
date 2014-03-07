package org.jbpm.designer.web.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.DocumentRoot;
import org.eclipse.bpmn2.ExtensionAttributeValue;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.RootElement;
import org.eclipse.bpmn2.util.Bpmn2ResourceFactoryImpl;
import org.eclipse.bpmn2.util.Bpmn2ResourceImpl;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Diagnostic;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.jboss.drools.DroolsPackage;

import com.doctusoft.jbpm.ds.DsPackage;
import com.doctusoft.jbpm.ds.DsProcessMetadataType;

public class DsExtModelInit extends HttpServlet {

	private static final String INIT_DEFINITION = "initialize.bpmn2";

	/**
	 * @see javax.servlet.GenericServlet#init()
	 */
	@Override
	public void init() throws ServletException {
		super.init();
		System.out.println("#### - initializing BPMN2 metamodel");
		initMetaModel();
		System.out.println("#### - initialization finished");

	}

	/*
	 * public static void main(String[] args) { final DsExtModelInit init = new DsExtModelInit(); init.initMetaModel();
	 * }
	 */
	private void initMetaModel() {
		Definitions definitions;
		try {
			definitions = getDefinitions(this.getClass().getClassLoader().getResourceAsStream(INIT_DEFINITION));

			StringWriter sw = new StringWriter();
			for (RootElement rootElement : definitions.getRootElements()) {
				if (rootElement instanceof Process) {
					Process process = (Process) rootElement;
					DsProcessMetadataType x = getMetadata(process, DsProcessMetadataType.class,
					        DsPackage.Literals.DOCUMENT_ROOT__DS_PROCESS_METADATA);
					sw.append("procmeta: " + x);
				}
			}
			System.out.println("########### - parsing result: " + sw.toString());

		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new RuntimeException("Unhandled exception!", e);
		}
	}

	private <T> T getMetadata(final BaseElement element, final Class<T> extensionClass, final EReference ref) {
		if (element.getExtensionValues() != null && element.getExtensionValues().size() > 0) {
			for (ExtensionAttributeValue extattrval : element.getExtensionValues()) {
				final FeatureMap extensionElements = extattrval.getValue();
				@SuppressWarnings("unchecked")
				final List<T> metadata = (List<T>) extensionElements.get(ref, true);
				if (metadata.size() > 0) {
					return metadata.get(0);
				}
			}
		}
		return null;
	}

	/**
	 * @param resourceAsStream
	 * @return
	 */
	private Definitions getDefinitions(final InputStream is) {
		final ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
		        .put(Resource.Factory.Registry.DEFAULT_EXTENSION, new Bpmn2ResourceFactoryImpl());
		resourceSet.getPackageRegistry().put("http://www.omg.org/spec/BPMN/20100524/MODEL", Bpmn2Package.eINSTANCE);
		resourceSet.getPackageRegistry().put("http://www.jboss.org/drools", DroolsPackage.eINSTANCE);
		resourceSet.getPackageRegistry().put("http://www.doctusoft.com/jbpm/extmodel", DsPackage.eINSTANCE);

		final Bpmn2ResourceImpl resource = (Bpmn2ResourceImpl) resourceSet.createResource(URI
		        .createURI("inputStream://dummyUriWithValidSuffix.xml"));
		resource.getDefaultLoadOptions().put(Bpmn2ResourceImpl.OPTION_ENCODING, "UTF-8");
		resource.setEncoding("UTF-8");
		final Map<String, Object> options = new HashMap<String, Object>();
		options.put(Bpmn2ResourceImpl.OPTION_ENCODING, "UTF-8");

		try {
			resource.load(is, options);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		final EList<Diagnostic> warnings = resource.getWarnings();
		if (warnings != null && !warnings.isEmpty()) {
			for (Diagnostic diagnostic : warnings) {
				System.out.println("Warning: " + diagnostic.getMessage());
			}
		}

		final EList<Diagnostic> errors = resource.getErrors();
		if (errors != null && !errors.isEmpty()) {
			for (Diagnostic diagnostic : errors) {
				System.out.println("Error: " + diagnostic.getMessage());
			}
			throw new IllegalStateException("Error parsing process definition");
		}

		return ((DocumentRoot) resource.getContents().get(0)).getDefinitions();
	}

}
