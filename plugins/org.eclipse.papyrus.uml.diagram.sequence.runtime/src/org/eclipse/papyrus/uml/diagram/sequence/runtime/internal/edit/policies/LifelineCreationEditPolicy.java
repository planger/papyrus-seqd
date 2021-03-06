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
 *   
 *****************************************************************************/

package org.eclipse.papyrus.uml.diagram.sequence.runtime.internal.edit.policies;

import java.util.Optional;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.UnexecutableCommand;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gmf.runtime.emf.type.core.IElementType;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.papyrus.uml.diagram.sequence.figure.HeaderFigure;
import org.eclipse.papyrus.uml.interaction.model.MElement;
import org.eclipse.papyrus.uml.interaction.model.MInteraction;
import org.eclipse.papyrus.uml.interaction.model.MLifeline;
import org.eclipse.papyrus.uml.interaction.model.MObject;
import org.eclipse.papyrus.uml.interaction.model.util.SequenceDiagramSwitch;
import org.eclipse.papyrus.uml.service.types.element.UMLElementTypes;
import org.eclipse.papyrus.uml.service.types.utils.ElementUtil;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Lifeline;

/**
 * Lifeline creation edit policy based on the <em>Logical Model</em>.
 */
public class LifelineCreationEditPolicy extends LogicalModelCreationEditPolicy {

	@Override
	protected Optional<org.eclipse.emf.common.command.Command> getCreationCommand(MInteraction interaction,
			Element parentElement, View parentView, Point location, Dimension size, IElementType type) {

		Optional<MLifeline> mLifeline = interaction.getLifeline((Lifeline)parentElement);

		// compute command for the given request
		SequenceDiagramSwitch<Command> commandSwitch = new SequenceDiagramSwitch<Command>() {

			@Override
			public Command caseMLifeline(MLifeline lifeline) {
				if (ElementUtil.isTypeOf(type, UMLElementTypes.BEHAVIOR_EXECUTION_SPECIFICATION)) {
					Optional<MElement<?>> before = lifeline.elementAt(location.y());

					int offset = location.y();

					// Account for the header's margin to ensure that the execution is placed
					// exactly where the mouse cursor put it.
					// FIXME: Locate where this margin actually is; don't assume the default
					offset = offset - HeaderFigure.DEFAULT_MARGIN_HEIGHT;

					if (before.isPresent()) {
						// We know the top exists because that's how we found the 'before' element
						offset = offset - before.get().getTop().getAsInt();
					}

					return lifeline.insertExecutionAfter(before.orElse(lifeline), offset,
							size != null ? size.height : 40,
							// Creates a behavior execution by default when there's no specification
							null);
				}
				return super.caseMLifeline(lifeline);
			}

			@Override
			public Command defaultCase(MObject object) {
				return UnexecutableCommand.INSTANCE;
			}
		};

		return mLifeline.map(commandSwitch::doSwitch);

	}

	@Override
	protected Point getRelativeLocation(Point location) {
		Point result = super.getRelativeLocation(location);

		// And adjust for the lifeline header
		GraphicalEditPart header = (GraphicalEditPart)getHost().getParent();
		result.translate(0, -header.getFigure().getBounds().height());

		return result;
	}

}
