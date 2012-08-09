/**
 * 
 */
package org.nnsoft.guice.autobind.annotations.features;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Singleton;

import org.nnsoft.guice.autobind.annotations.GuiceModule;
import org.nnsoft.guice.autobind.install.BindingStage;
import org.nnsoft.guice.autobind.install.bindjob.BindingJob;
import org.nnsoft.guice.autobind.install.bindjob.ModuleBindingJob;
import org.nnsoft.guice.autobind.scanner.features.BindingScannerFeature;

import com.google.inject.Module;


@Singleton
public class ModuleBindingFeature extends BindingScannerFeature {
	private Logger _logger = Logger.getLogger(ModuleBindingFeature.class.getName());

	@Override
	public BindingStage accept(Class<Object> annotatedClass, Map<String, Annotation> annotations) {
		if (annotations.containsKey(GuiceModule.class.getName())) {
			GuiceModule module = (GuiceModule) annotations.get(GuiceModule.class.getName());
			return module.stage();
		}
		return BindingStage.IGNORE;
	}

	@Override
	public void process(final Class<Object> annotatedClass, Map<String, Annotation> annotations) {
		BindingJob job = new ModuleBindingJob(annotatedClass.getName());
		if(!tracer.contains(job)){
			if (_logger.isLoggable(Level.INFO)) {
				_logger.info("Installing Module: " + annotatedClass.getName());
			}
			synchronized (_binder) {
				_binder.install((Module) injector.getInstance(annotatedClass));
			}			
		}else {
			if (_logger.isLoggable(Level.FINE)) {
				_logger.log(Level.FINE, "Ignoring BindingJob \"" + job.toString()
						+ "\", because it was already bound.", new Exception("Ignoring BindingJob \"" + job.toString()
							+ "\", because it was already bound."));
			} else if (_logger.isLoggable(Level.INFO)) {
				_logger.log(Level.INFO, "Ignoring BindingJob \"" + job.toString()
						+ "\", because it was already bound.");
			}
		}
	}
}