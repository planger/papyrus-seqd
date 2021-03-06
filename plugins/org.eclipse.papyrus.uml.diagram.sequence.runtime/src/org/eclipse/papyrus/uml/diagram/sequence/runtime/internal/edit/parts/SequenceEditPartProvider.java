/*****************************************************************************
 * Copyright (c) 2018 EclipseSource and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   EclipseSource - Initial API and implementation
 *****************************************************************************/
package org.eclipse.papyrus.uml.diagram.sequence.runtime.internal.edit.parts;

import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.services.editpart.AbstractEditPartProvider;
import org.eclipse.gmf.runtime.notation.View;

public class SequenceEditPartProvider extends AbstractEditPartProvider {

	@Override
	protected Class<? extends IGraphicalEditPart> getDiagramEditPartClass(View view) {
		if (RepresentationKind.MODEL_ID.equals(view.getType())) {
			return LightweightSequenceDiagramEditPart.class;
		} else if (RepresentationKind.DIAGRAM_ID.equals(view.getType())) {
			return LightweightSequenceDiagramEditPart.class;
		}
		return null;
	}

	@Override
	protected Class<? extends IGraphicalEditPart> getEdgeEditPartClass(View view) {
		switch (view.getType()) {
			case RepresentationKind.MESSAGE_ID:
				return MessageEditPart.class;
			case RepresentationKind.GENERAL_ORDERING_ID:
				return GeneralOrderingEditPart.class;
		}

		return null;
	}

	@Override
	protected Class<? extends IGraphicalEditPart> getNodeEditPartClass(View view) {
		switch (view.getType()) {
			case RepresentationKind.INTERACTION_ID:
				return InteractionEditPart.class;
			case RepresentationKind.LIFELINE_HEADER_ID:
				return LifelineHeaderEditPart.class;
			case RepresentationKind.LIFELINE_HEADER_COMPARTMENT_ID:
				return LifelineHeaderCompartmentEditPart.class;
			case RepresentationKind.LIFELINE_BODY_ID:
				return LifelineBodyEditPart.class;
			case RepresentationKind.LIFELINE_NAME_ID:
				return LifelineNameEditPart.class;
			case RepresentationKind.INTERACTION_NAME_ID:
				return InteractionNameEditPart.class;
			case RepresentationKind.INTERACTION_COMPARTMENT_ID:
				return InteractionCompartmentEditPart.class;
			case RepresentationKind.EXECUTION_SPECIFICATION_ID:
				return ExecutionSpecificationEditPart.class;
			case RepresentationKind.STATE_INVARIANT_ID:
				return StateInvariantEditPart.class;
		}

		return null;
	}

}
