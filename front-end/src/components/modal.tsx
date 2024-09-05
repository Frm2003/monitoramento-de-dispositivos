import React, {
    forwardRef,
    useImperativeHandle,
    useRef,
    ReactNode,
} from 'react';
import modalStyle from '@/styles/modal.module.css';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faClose } from '@fortawesome/free-solid-svg-icons';

interface ModalProps {
    children: ReactNode;
    fecharModal: () => void;
    show: boolean;
}

export interface ModalHandles {
    genericClose: () => void;
}

const Modal = forwardRef<ModalHandles, ModalProps>(
    ({ children, fecharModal, show }, ref) => {
        const modalRef = useRef<HTMLDivElement>(null);

        const genericClose = () => {
            if (modalRef.current) {
                const animation = modalRef.current.animate(
                    [{ marginTop: '0%' }, { marginTop: '-300%' }],
                    {
                        duration: 250,
                        fill: 'forwards',
                    }
                );

                animation.onfinish = () => fecharModal();
            }
        };

        useImperativeHandle(ref, () => ({
            genericClose,
        }));

        const html = (
            <section ref={modalRef} className={modalStyle.layout}>
                <div className={modalStyle.body}>
                    <article style={{ textAlign: 'right' }}>
                        <FontAwesomeIcon
                            icon={faClose}
                            size="2x"
                            onClick={genericClose} // Use the method defined above
                        />
                    </article>
                    {children}
                </div>
            </section>
        );

        return show ? html : null;
    }
);

Modal.displayName = 'Modal';

export default Modal;
