/*****************************************************************************
 * Copyright (c) 2018 Christian W. Damus and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Christian W. Damus - Initial API and implementation
 *****************************************************************************/

package org.eclipse.papyrus.uml.interaction.internal.model.commands;

import org.eclipse.emf.common.command.Command;
import org.eclipse.papyrus.uml.interaction.internal.model.impl.MInteractionImpl;
import org.eclipse.papyrus.uml.interaction.model.CreationCommand;
import org.eclipse.papyrus.uml.interaction.model.CreationParameters;
import org.eclipse.papyrus.uml.interaction.model.spi.SemanticHelper;
import org.eclipse.uml2.uml.Lifeline;
import org.eclipse.uml2.uml.UMLPackage;

/**
 * A lifeline creation operation.
 *
 * @author Christian W. Damus
 */
public class AddLifelineCommand extends ModelCommand<MInteractionImpl> implements CreationCommand<Lifeline> {

	private final int xOffset;

	private final int height;

	private CreationCommand<Lifeline> resultCommand;

	/**
	 * Initializes me.
	 * 
	 * @param owner
	 *            the interaction in which to create the lifeline
	 * @param xOffset
	 *            the offset from the left edge of the interaction frame at which to create the lifeline
	 * @param height
	 *            the height of the lifeline, or {@code -1} for the default
	 */
	public AddLifelineCommand(MInteractionImpl owner, int xOffset, int height) {
		super(owner);

		this.xOffset = xOffset;
		this.height = height;
	}

	@Override
	public Lifeline getNewObject() {
		return (resultCommand == null) ? null : resultCommand.getNewObject();
	}

	@Override
	protected Command createCommand() {
		SemanticHelper semantics = semanticHelper();
		CreationParameters params = CreationParameters.in(getTarget().getElement(),
				UMLPackage.Literals.INTERACTION__LIFELINE);
		resultCommand = semantics.createLifeline(params);

		Command result = resultCommand.chain(diagramHelper().createLifelineShape(resultCommand,
				getTarget().getDiagramView().get(), xOffset, height));

		// TODO: If required for where the lifeline is being put, insert horizontal space

		return result;
	}
}
