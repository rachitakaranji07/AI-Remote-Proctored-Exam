from flask import Blueprint, request, jsonify
from flask_jwt_extended import (
    jwt_required,
    get_jwt,
    get_jwt_identity
)

from database import db
from models.violation_model import Violation
from models.exam_model import Exam


violation_bp = Blueprint(
    "violation",
    __name__
)


@violation_bp.route("/report", methods=["POST"])
@jwt_required()
def report_violation():

    # Only students can generate violations
    claims = get_jwt()

    if claims.get("role") != "student":
        return jsonify({
            "success": False,
            "message": "Access denied. Student only."
        }), 403


    student_id = int(
        get_jwt_identity()
    )

    data = request.get_json()


    if not data:
        return jsonify({
            "success": False,
            "message": "No violation data provided."
        }), 400


    exam_id = data.get(
        "exam_id"
    )

    violation_type = data.get(
        "violation_type"
    )

    description = data.get(
        "description"
    )


    if not exam_id or not violation_type:

        return jsonify({
            "success": False,
            "message":
                "Exam ID and violation type are required."
        }), 400


    # Check exam exists
    exam = db.session.get(
        Exam,
        exam_id
    )


    if not exam:

        return jsonify({
            "success": False,
            "message": "Exam not found."
        }), 404


    violation = Violation(

        student_id=student_id,

        exam_id=exam_id,

        violation_type=violation_type,

        description=description
    )


    try:

        db.session.add(
            violation
        )

        db.session.commit()


    except Exception as e:

        db.session.rollback()

        return jsonify({
            "success": False,
            "message":
                "Failed to save violation."
        }), 500


    return jsonify({

        "success": True,

        "message":
            "Violation recorded.",

        "violation": {

            "id":
                violation.id,

            "exam_id":
                violation.exam_id,

            "violation_type":
                violation.violation_type,

            "description":
                violation.description
        }

    }), 201