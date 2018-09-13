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

package org.eclipse.papyrus.uml.interaction.internal.model.spi.impl;

import org.eclipse.gmf.runtime.notation.Anchor;
import org.eclipse.gmf.runtime.notation.Connector;
import org.eclipse.gmf.runtime.notation.IdentityAnchor;
import org.eclipse.gmf.runtime.notation.NotationPackage;
import org.eclipse.gmf.runtime.notation.Shape;
import org.eclipse.papyrus.uml.interaction.model.spi.LayoutHelper;
import org.eclipse.papyrus.uml.interaction.model.spi.ViewTypes;

/**
 * A black-box factory for connection anchors in the sequence diagram.
 */
class AnchorFactory {

	private NullAnchorBuilder nullAnchor;

	private LifelineBodyAnchorBuilder lifelineBodyBuilder;

	private LifelineHeadAnchorBuilder lifelineHeadBuilder;

	private ExecutionSpecificationAnchorBuilder execSpecBuilder;

	private final Connector connector;

	private final LayoutHelper layout;

	/**
	 * Initializes me with the {@code connector} for which I create anchors and a {@code layout} helper for
	 * geometric calculations.
	 * 
	 * @param connector
	 *            my connector
	 * @param layout
	 *            my layout helper
	 */
	AnchorFactory(Connector connector, LayoutHelper layout) {
		super();

		this.connector = connector;
		this.layout = layout;
	}

	public AnchorBuilder builderFor(Shape shape) {
		AnchorBuilder result;

		if (shape.getType() == null) {
			result = defaultAnchorBuilder();
		} else {
			switch (shape.getType()) {
				case ViewTypes.LIFELINE_BODY:
					result = lifelineBodyBuilder();
					break;
				case ViewTypes.LIFELINE_HEADER:
					result = lifelineHeadBuilder();
					break;
				case ViewTypes.EXECUTION_SPECIFICATION:
					result = executionSpecificationBuilder();
					break;
				default:
					result = defaultAnchorBuilder();
			}
		}

		return result;
	}

	public AnchorBuilder defaultAnchorBuilder() {
		if (nullAnchor == null) {
			nullAnchor = new NullAnchorBuilder();
		}
		return nullAnchor;
	}

	public AnchorBuilder lifelineBodyBuilder() {
		if (lifelineBodyBuilder == null) {
			lifelineBodyBuilder = new LifelineBodyAnchorBuilder();
		}
		return lifelineBodyBuilder;
	}

	public AnchorBuilder lifelineHeadBuilder() {
		if (lifelineHeadBuilder == null) {
			lifelineHeadBuilder = new LifelineHeadAnchorBuilder();
		}
		return lifelineHeadBuilder;
	}

	public AnchorBuilder executionSpecificationBuilder() {
		if (execSpecBuilder == null) {
			execSpecBuilder = new ExecutionSpecificationAnchorBuilder();
		}
		return execSpecBuilder;
	}

	public static boolean isExecutionSpecificationStart(Anchor anchor) {
		return (anchor instanceof IdentityAnchor) && "start".equals(((IdentityAnchor)anchor).getId());
	}

	public static boolean isExecutionSpecificationFinish(Anchor anchor) {
		return (anchor instanceof IdentityAnchor) && "end".equals(((IdentityAnchor)anchor).getId());
	}

	//
	// Nested types
	//

	public abstract class AnchorBuilder {
		boolean isConnectionSource;

		Shape sourceView;

		Shape targetView;

		int distance;

		private AnchorBuilder() {
			super();
		}

		public AnchorBuilder from(Shape view) {
			sourceView = view;
			return this;
		}

		public AnchorBuilder to(Shape view) {
			targetView = view;
			return this;
		}

		public AnchorBuilder sourceEnd() {
			isConnectionSource = true;
			return this;
		}

		public AnchorBuilder targetEnd() {
			isConnectionSource = false;
			return this;
		}

		boolean isLeftToRight() {
			return layout.getLeft(sourceView) <= layout.getLeft(targetView);
		}

		Shape getAnchorView() {
			return isConnectionSource ? sourceView : targetView;
		}

		public AnchorBuilder at(int yOffset) {
			this.distance = yOffset;
			return this;
		}

		public final Anchor build() {
			IdentityAnchor result = isConnectionSource
					? (IdentityAnchor)connector.createSourceAnchor(NotationPackage.Literals.IDENTITY_ANCHOR)
					: (IdentityAnchor)connector.createTargetAnchor(NotationPackage.Literals.IDENTITY_ANCHOR);
			result.setId(computeIdentity());
			sourceView = null;
			targetView = null;
			return result;
		}

		protected abstract String computeIdentity();
	}

	private final class NullAnchorBuilder extends AnchorBuilder {
		NullAnchorBuilder() {
			super();
		}

		@Override
		public String computeIdentity() {
			return ""; //$NON-NLS-1$
		}
	}

	private final class LifelineBodyAnchorBuilder extends AnchorBuilder {
		private LifelineBodyAnchorBuilder() {
			super();
		}

		@Override
		public String computeIdentity() {
			return Integer.toString(distance);
		}
	}

	private final class LifelineHeadAnchorBuilder extends AnchorBuilder {
		private LifelineHeadAnchorBuilder() {
			super();
		}

		@Override
		public String computeIdentity() {
			String result;

			// Which direction?
			if (isLeftToRight() == isConnectionSource) {
				result = "right;" + distance;
			} else {
				result = "left;" + distance;
			}

			return result;
		}
	}

	private final class ExecutionSpecificationAnchorBuilder extends AnchorBuilder {
		private ExecutionSpecificationAnchorBuilder() {
			super();
		}

		@Override
		public String computeIdentity() {
			String result;

			// Is it the top or bottom?
			if (distance < 1) {
				result = "start";
			} else if (distance >= layout.getHeight(getAnchorView())) {
				result = "end";
			} else // Which direction?
			if (isLeftToRight() == isConnectionSource) {
				result = "right;" + distance;
			} else {
				result = "left;" + distance;
			}

			return result;
		}
	}
}